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
import org.bouncycastle.util.Arrays;
import org.ldcgc.backend.db.model.resources.Consumable;
import org.ldcgc.backend.db.model.resources.Tool;
import org.ldcgc.backend.db.repository.resources.ConsumableRepository;
import org.ldcgc.backend.db.repository.resources.ToolRepository;
import org.ldcgc.backend.exception.RequestException;
import org.ldcgc.backend.payload.mapper.resources.consumable.ConsumableMapper;
import org.ldcgc.backend.payload.mapper.resources.tool.ToolMapper;
import org.ldcgc.backend.service.resources.upload.UploadService;
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
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Slf4j
@Component
@RequiredArgsConstructor
public class UploadServiceImpl implements UploadService {

    private final ToolRepository toolRepository;
    private final ConsumableRepository consumableRepository;

    @Value("classpath:gdrive_secret.json") Resource CREDENTIALS_FILE;
    private final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();


    public ResponseEntity<?> uploadToolImages(String toolBarcode, boolean cleanExisting, MultipartFile[] images) throws GeneralSecurityException, IOException {
        Tool tool = toolRepository.findFirstByBarcode(toolBarcode).orElseThrow(() ->
            new RequestException(HttpStatus.NOT_FOUND, Messages.Error.TOOL_NOT_FOUND));

        if(cleanExisting)
            cleanFromGDrive(tool.getUrlImages());

        // folder LDC->G8->Tools
        final String toolsFolderId = "1ibDAscWYHG_4FJV4ciqtu1U4GhT75j48";
        String[] urlImages = uploadToGDrive(images, toolsFolderId);
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

        // folder LDC->G8->Consumables
        final String consumablesFolderId = "1w2uNCH0iDAzcaU0O1NVTeRuBIfGV9ZzR";
        String[] urlImages = uploadToGDrive(images, consumablesFolderId);
        consumable.setUrlImages(cleanExisting
            ? urlImages
            : Stream.concat(Stream.of(consumable.getUrlImages()), Stream.of(urlImages)).toArray(String[]::new));

        consumable = consumableRepository.saveAndFlush(consumable);

        return Constructor.buildResponseMessageObject(HttpStatus.CREATED, Messages.Info.CONSUMABLE_UPDATED, ConsumableMapper.MAPPER.toDto(consumable));
    }

    private void cleanFromGDrive(String[] urlImages) throws GeneralSecurityException {
        if(urlImages == null) return;

        for(String urlImage : urlImages) {
            String imageId = urlImage.split("id=")[1];
            try {
                getInstance().files().delete(imageId).execute();
            } catch (IOException e) {
                log.warn(e.getMessage());
            }
        }
    }

    private String[] uploadToGDrive(MultipartFile[] images, String folderId) throws GeneralSecurityException, IOException {
        List<String> urlImages = new ArrayList<>();

        for(MultipartFile image : images) {

            image = compressImage(image);

            File fileMetadata = new File();
            fileMetadata.setParents(Collections.singletonList(folderId));
            fileMetadata.setName(image.getOriginalFilename());

            InputStreamContent mediacontent = new InputStreamContent(image.getContentType(), new ByteArrayInputStream(image.getBytes()));
            File uploadFile = getInstance()
                .files()
                .create(fileMetadata, mediacontent)
                .setFields("id").execute();
            //System.out.println(uploadFile);
            urlImages.add(String.format("https://drive.google.com/uc?export=view&id=%s", uploadFile.getId()));
        }

        return urlImages.toArray(new String[0]);

    }

    private MultipartFile compressImage(MultipartFile mpImage) throws IOException {
        float quality = 0.8f;
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
        param.setCompressionQuality(quality);

        // Write the compressed image to the output stream
        writer.setOutput(ImageIO.createImageOutputStream(outputStream));
        writer.write(null, new IIOImage(resizedImage, null, null), param);

        // Cleanup resources
        writer.dispose();

        //ImageIO.write(resizedImage, Objects.requireNonNull(mpImage.getContentType()), outputStream);

        return CompressedMultipartFile.builder()
            .name(mpImage.getName())
            .originalFilename(mpImage.getOriginalFilename())
            .contentType(mpImage.getContentType())
            .input(outputStream.toByteArray()).build();

    }

    private Drive getInstance() throws GeneralSecurityException, IOException {
        // Load client secrets
        InputStream in = CREDENTIALS_FILE.getInputStream();

        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        final List<String> SCOPE = Collections.singletonList(DriveScopes.DRIVE_FILE);
        GoogleCredentials credentials = GoogleCredentials.fromStream(in).createScoped(SCOPE);
        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);

        return new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, requestInitializer)
            .setApplicationName("ldcgc-backend")
            .build();

        /*GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow
            .Builder(HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
            .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
            .setAccessType("offline")
            .build();

        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        //returns an authorized Credential object.
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
         */
    }

}
