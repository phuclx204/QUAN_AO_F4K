package org.example.quan_ao_f4k.model.general;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "image")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name_file")
    private String nameFile;

    @Column(name = "path")
    private String path;

    @Column(name = "size", nullable = false)
    private Long size;

    @JsonIgnore
    @Column(name = "id_parent")
    private Long idParent;

    @Column(name = "status", nullable = false)
    private Integer status =1;

    @Column(name = "tabble_code", nullable = false)
    private String tableCode;
}
