package com.example.phase_03.entity;

import com.example.phase_03.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@SequenceGenerator(name = "id_generator", sequenceName = "sub_assistance_sequence")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class SubAssistance extends BaseEntity {
    private String title;
    @Column(name = "base_Price")
    private long basePrice;
    @ManyToMany
    private List<Technician> technicians;
    @ManyToOne
    private Assistance assistance;
    @OneToMany(mappedBy = "subAssistance")
    private List<Order> orders;
    private String about;

    public String toString() {
        return "\n\t\ttitle = " + this.getTitle() +
                "\n\t\tassistance_category = " + this.getAssistance() +
                "\n\t\tbase_Price = " + this.getBasePrice() +
                "\n\t\tabout = " + this.getAbout() + "\n";
    }
}
