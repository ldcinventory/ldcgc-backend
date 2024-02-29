package org.ldcgc.backend.util.process;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Builder
@AllArgsConstructor
public class CompressedMultipartFile implements MultipartFile {

    private byte[] input;
    @Getter private String name;
    @Getter private String originalFilename;
    @Getter private String contentType;

    public boolean isEmpty() {
        return input == null || input.length == 0;
    }

    public long getSize() {
        return input.length;
    }

    public byte @NotNull [] getBytes() {
        return input;
    }

    public @NotNull InputStream getInputStream() {
        return new ByteArrayInputStream(input);
    }

    public void transferTo(@NotNull File dest) throws IllegalStateException, IOException {
        try (FileOutputStream fos = new FileOutputStream(dest)) {
            fos.write(input);
        }
    }
}
