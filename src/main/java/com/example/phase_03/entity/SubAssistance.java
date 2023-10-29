package com.example.phase_03.entity;

import com.example.phase_03.entity.base.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.validator.constraints.Range;

import java.util.List;

@Entity
@SequenceGenerator(name = "id_generator", sequenceName = "sub_assistance_sequence")
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class SubAssistance extends BaseEntity {
    @NotNull(message = "Sub-assistance title can not be null")
    private String title;
    @Range(min = 0, message = "Base price can not be negative")
    @Column(name = "base_Price")
    private long basePrice;
    @ManyToMany
    private List<Technician> technicians;
    @ManyToOne
    private Assistance assistance;
    @OneToMany(mappedBy = "subAssistance")
    private List<Order> orders;
    @NotNull(message = "Sub-assistance should have some descriptions")
    private String about;

    public String toString() {
        return "\n\t\ttitle = " + this.getTitle() +
                "\n\t\tassistance_category = " + this.getAssistance() +
                "\n\t\tbase_Price = " + this.getBasePrice() +
                "\n\t\tabout = " + this.getAbout() + "\n";
    }

    public @NotNull(message = "Sub-assistance title can not be null") String getTitle() {
        return this.title;
    }

    public @Range(min = 0, message = "Base price can not be negative") long getBasePrice() {
        return this.basePrice;
    }

    public List<Technician> getTechnicians() {
        return this.technicians;
    }

    public Assistance getAssistance() {
        return this.assistance;
    }

    public List<Order> getOrders() {
        return this.orders;
    }

    public @NotNull(message = "Sub-assistance should have some descriptions") String getAbout() {
        return this.about;
    }
}
