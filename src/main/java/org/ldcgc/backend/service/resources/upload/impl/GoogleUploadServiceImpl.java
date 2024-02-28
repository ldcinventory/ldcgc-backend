package org.ldcgc.backend.service.resources.upload.impl;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.Range;
import org.jetbrains.annotations.NotNull;
import org.ldcgc.backend.db.model.resources.Consumable;
import org.ldcgc.backend.db.model.resources.Tool;
import org.ldcgc.backend.db.repository.resources.ConsumableRepository;
import org.ldcgc.backend.db.repository.resources.ToolRepository;
import org.ldcgc.backend.exception.RequestException;
import org.ldcgc.backend.payload.mapper.resources.consumable.ConsumableMapper;
import org.ldcgc.backend.payload.mapper.resources.tool.ToolMapper;
import org.ldcgc.backend.service.resources.upload.GoogleUploadService;
import org.ldcgc.backend.util.constants.GoogleConstants;
import org.ldcgc.backend.util.creation.Constructor;
import org.ldcgc.backend.util.process.CompressedMultipartFile;
import org.ldcgc.backend.util.retrieving.Messages;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Slf4j
@Component
@RequiredArgsConstructor
public class GoogleUploadServiceImpl implements GoogleUploadService {

    private final ToolRepository toolRepository;
    private final ConsumableRepository consumableRepository;

    @Value("classpath:gdrive_secret.json") Resource CREDENTIALS_FILE;
    @Value("${IMAGE_QUALITY:0.8f}") private float IMAGE_QUALITY;
    private final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    public ResponseEntity<?> uploadToolImages(String toolBarcode, boolean cleanExisting, MultipartFile[] images) throws GeneralSecurityException, IOException {
        Tool tool = toolRepository.findFirstByBarcode(toolBarcode).orElseThrow(() ->
            new RequestException(HttpStatus.NOT_FOUND, Messages.Error.TOOL_NOT_FOUND));

        if(cleanExisting)
            cleanFromGDrive(tool.getUrlImages());

        // folder LDC -> G8 -> Tools
        String[] urlImages = uploadToGDrive(images, GoogleConstants.DRIVE_TOOLS_FOLDER_ID);
        tool.setUrlImages(cleanExisting
            ? urlImages
            : Stream.concat(Stream.of(tool.getUrlImages()), Stream.of(urlImages)).toArray(String[]::new));

        tool = toolRepository.saveAndFlush(tool);

        return Constructor.buildResponseMessageObject(HttpStatus.CREATED, Messages.Info.TOOL_UPDATED, ToolMapper.MAPPER.toDto(tool));
    }

    public ResponseEntity<?> uploadConsumableImages(String consumableBarcode, boolean cleanExisting, MultipartFile[] images) throws GeneralSecurityException, IOException {
        Consumable consumable = consumableRepository.findByBarcode(consumableBarcode).orElseThrow(() ->
            new RequestException(HttpStatus.NOT_FOUND, Messages.Error.CONSUMABLE_NOT_FOUND));

        if(cleanExisting)
            cleanFromGDrive(consumable.getUrlImages());

        // folder LDC -> G8 -> Consumables
        String[] urlImages = uploadToGDrive(images, GoogleConstants.DRIVE_CONSUMABLES_FOLDER_ID);
        consumable.setUrlImages(cleanExisting
            ? urlImages
            : Stream.concat(Stream.of(ObjectUtils.defaultIfNull(consumable.getUrlImages(), new String[0])), Stream.of(urlImages)).toArray(String[]::new));

        consumable = consumableRepository.saveAndFlush(consumable);

        return Constructor.buildResponseMessageObject(HttpStatus.CREATED, Messages.Info.CONSUMABLE_UPDATED, ConsumableMapper.MAPPER.toDto(consumable));
    }

    public ResponseEntity<?> cleanToolImages(String toolBarcode, String[] imageIds) throws GeneralSecurityException {
        Tool tool = toolRepository.findFirstByBarcode(toolBarcode).orElseThrow(() ->
            new RequestException(HttpStatus.NOT_FOUND, Messages.Error.TOOL_NOT_FOUND));

        if (tool.getUrlImages() == null)
            return Constructor.buildResponseMessageObject(HttpStatus.CREATED, Messages.Info.TOOL_UNTOUCHED, ToolMapper.MAPPER.toDto(tool));

        if (imageIds == null) { // clean all images
            cleanFromGDrive(tool.getUrlImages());
            tool.setUrlImages(null);
        } else { // clean images from array
            List<String> imageList = cleanImagesFromArray(imageIds, tool);
            tool.setUrlImages(imageList.toArray(new String[0]));
        }

        tool = toolRepository.saveAndFlush(tool);

        return Constructor.buildResponseMessageObject(HttpStatus.CREATED, Messages.Info.TOOL_IMAGES_UPDATED, ToolMapper.MAPPER.toDto(tool));
    }

    public ResponseEntity<?> cleanConsumableImages(String consumableBarcode, String[] imageIds) throws GeneralSecurityException {
        Consumable consumable = consumableRepository.findByBarcode(consumableBarcode).orElseThrow(() ->
            new RequestException(HttpStatus.NOT_FOUND, Messages.Error.CONSUMABLE_NOT_FOUND));

        if (consumable.getUrlImages() == null)
            return Constructor.buildResponseMessageObject(HttpStatus.CREATED, Messages.Info.CONSUMABLE_UNTOUCHED, ConsumableMapper.MAPPER.toDto(consumable));

        if (imageIds == null) { // clean all images
            cleanFromGDrive(consumable.getUrlImages());
            consumable.setUrlImages(null);
        } else { // clean images from array
            List<String> imageList = cleanImagesFromArray(imageIds, consumable);
            consumable.setUrlImages(imageList.toArray(new String[0]));
        }

        consumable = consumableRepository.saveAndFlush(consumable);

        return Constructor.buildResponseMessageObject(HttpStatus.CREATED, Messages.Info.CONSUMABLE_IMAGES_UPDATED, ConsumableMapper.MAPPER.toDto(consumable));
    }

    @NotNull
    private List<String> cleanImagesFromArray(String[] imageIds, Object resource) throws GeneralSecurityException {
        List<String> imageList;
        if(resource instanceof Tool)
            imageList = new ArrayList<>(Arrays.stream(((Tool) resource).getUrlImages()).toList());
        else if(resource instanceof Consumable)
            imageList = new ArrayList<>(Arrays.stream(((Consumable) resource).getUrlImages()).toList());
        else
            throw new RequestException(HttpStatus.UNPROCESSABLE_ENTITY, Messages.Error.CLEAN_IMAGES_ENTITY_CANT_BE_CASTABLE);

        for (String image : imageIds)
            if (imageList.contains(image)) {
                cleanFromGDrive(image);
                imageList.remove(image);
            } else
                throw new RequestException(HttpStatus.NOT_FOUND, String.format(Messages.Error.TOOL_IMAGE_INFORMED_NOT_FOUND, image));
        return imageList;
    }

    private Drive getGDriveInstance() throws GeneralSecurityException, IOException {
        // Load client secrets
        InputStream in = CREDENTIALS_FILE.getInputStream();

        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        final List<String> SCOPE = Collections.singletonList(DriveScopes.DRIVE_FILE);
        GoogleCredentials credentials = GoogleCredentials.fromStream(in).createScoped(SCOPE);
        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);

        return new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, requestInitializer)
            .setApplicationName("ldcgc-backend")
            .build();

    }

    private void cleanFromGDrive(String ...urlImages) throws GeneralSecurityException {
        if(urlImages == null) return;

        for(String urlImage : urlImages) {
            try {
                getGDriveInstance().files().delete(urlImage).execute();
            } catch (IOException e) {
                log.warn(e.getMessage());
            }
        }
    }

    private String[] uploadToGDrive(MultipartFile[] images, String folderId) throws GeneralSecurityException, IOException {
        List<String> urlImages = new ArrayList<>();

        for(MultipartFile image : images) {

            image = compressAndResizeImage(image);

            File fileMetadata = new File();
            fileMetadata.setParents(Collections.singletonList(folderId));
            fileMetadata.setName(image.getOriginalFilename());

            InputStreamContent mediacontent = new InputStreamContent(image.getContentType(), new ByteArrayInputStream(image.getBytes()));
            File uploadFile = getGDriveInstance()
                .files()
                .create(fileMetadata, mediacontent)
                .setFields("id").execute();
            urlImages.add(uploadFile.getId());
        }

        return urlImages.toArray(new String[0]);

    }

    private MultipartFile compressAndResizeImage(MultipartFile mpImage) throws IOException {
        if(Range.of(0.0f, 1.0f).contains(IMAGE_QUALITY))
            throw new RequestException(HttpStatus.INTERNAL_SERVER_ERROR, Messages.Error.IMAGE_QUALITY_DEFINITION_OUT_OF_RANGE);

        byte[] imageBytes = mpImage.getBytes();

        BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageBytes));
        float proportion = (float) Math.max(image.getHeight(), image.getWidth())
                         / (float) Math.min(image.getHeight(), image.getWidth());

        // landscape image
        int targetWidth = (int) (Math.min(1000, image.getWidth()) / proportion);
        int targetHeight = Math.min(1000, image.getWidth());

        // vertical image
        if(image.getHeight() < image.getWidth()) {
            targetWidth = Math.min(1000, image.getHeight());
            targetHeight = (int) (Math.min(1000, image.getHeight()) / proportion);
        }

        // Read the image into a BufferedImage
        BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(imageBytes));

        // Create a new scaled instance of the image
        Image scaledImage = originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);

        // Draw the scaled image onto the buffered image
        Graphics2D graphics2D = resizedImage.createGraphics();
        graphics2D.drawImage(scaledImage, 0, 0, targetWidth, targetHeight, null);
        graphics2D.dispose();

        // Compress the image
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName(Objects.requireNonNull(mpImage.getContentType()).split("/")[1]);
        ImageWriter writer = writers.next();
        ImageWriteParam param = writer.getDefaultWriteParam();

        // Set compression quality
        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        param.setCompressionQuality(IMAGE_QUALITY);

        // Write the compressed image to the output stream
        writer.setOutput(ImageIO.createImageOutputStream(outputStream));
        writer.write(null, new IIOImage(resizedImage, null, null), param);

        // Cleanup resources
        writer.dispose();

        return CompressedMultipartFile.builder()
            .name(mpImage.getName())
            .originalFilename(mpImage.getOriginalFilename())
            .contentType(mpImage.getContentType())
            .input(outputStream.toByteArray()).build();

    }

}
