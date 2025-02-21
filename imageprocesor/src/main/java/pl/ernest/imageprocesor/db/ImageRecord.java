package pl.ernest.imageprocesor.db;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@Table("images")
public class ImageRecord {

    @Id
    private Long id;

    @Column("fullpath")
    private String fullPath;

    @Column("miniaturepath")
    private String miniaturePath;

    public ImageRecord() { }

    public ImageRecord(String fullPath, String miniaturePath){
        this.fullPath = fullPath;
        this.miniaturePath = miniaturePath;
    }

    @Override
    public String toString() {
        return String.format(
                "ImageRecord[id=%d, path='%s', miniaturePath='%s']",
                id, fullPath, miniaturePath);
    }
}
