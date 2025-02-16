package pl.ernest.imageprocesor.db;

import org.springframework.data.annotation.Id;

public class ImageRecord {

    @Id
    private Long id;

    private final String fullPath;

    private final String miniaturePath;

    public ImageRecord(String fullPath, String miniaturePath){
        this.fullPath = fullPath;
        this.miniaturePath = miniaturePath;
    }

    public Long getId(){
        return this.id;
    }

    public void setId(Long id){
        this.id = id;
    }

    public String getFullPath(){
        return this.fullPath;
    }

    public String getMiniaturePath(){
        return this.miniaturePath;
    }

    @Override
    public String toString() {
        return String.format(
                "ImageRecord[id=%d, path='%s', miniaturePath='%s']",
                id, fullPath, miniaturePath);
    }
}
