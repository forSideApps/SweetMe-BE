package dev.sweetme.domain;

import dev.sweetme.domain.enums.RoomStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "company")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, unique = true, length = 50)
    private String slug;

    @Column(name = "accent_color", length = 20)
    private String accentColor;

    @Column(name = "display_order")
    private Integer displayOrder;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Room> rooms = new ArrayList<>();

    public void update(String name, String slug, String accentColor, Integer displayOrder) {
        this.name = name;
        this.slug = slug;
        this.accentColor = accentColor;
        this.displayOrder = displayOrder;
    }

    public long getOpenRoomCount() {
        return rooms.stream()
                .filter(r -> r.getStatus() == RoomStatus.OPEN)
                .count();
    }
}
