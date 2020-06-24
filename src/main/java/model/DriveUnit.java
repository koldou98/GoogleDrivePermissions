package model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Esquema de BBDD de las unidades de equipo
 */
@Entity
@Table(name = "DriveUnit")
public class DriveUnit implements Serializable {

    private static final long serialVersionUID = -4927803855946356457L;

    @Id
    @Column(name = "unit_id")
    private String unit_id;
    @Column(name = "kind")
    private String kind;
    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "driveUnit", cascade = CascadeType.ALL)
    private List<DriveFile> driveFiles = new ArrayList<>();

    public DriveUnit() {

    }

    public DriveUnit(String unit_id, String kind, String name) {
        this.unit_id = unit_id;
        this.kind = kind;
        this.name = name;
    }

    public String getUnit_id() {
        return unit_id;
    }

    public void setUnit_id(String id) {
        this.unit_id = id;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<DriveFile> getDriveFiles() {
        return driveFiles;
    }

    public void setDriveFiles(List<DriveFile> driveFiles) {
        this.driveFiles = driveFiles;
    }

    @Override
    public String toString() {
        return "DriveUnit{" +
                "unit_id='" + unit_id + '\'' +
                ", kind='" + kind + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
