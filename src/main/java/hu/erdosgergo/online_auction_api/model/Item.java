package hu.erdosgergo.online_auction_api.model;

import hu.erdosgergo.online_auction_api.enums.Category;
import hu.erdosgergo.online_auction_api.enums.ItemStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

@Data
@Entity
@Table(name = "ITEMS")
@FieldNameConstants
@AllArgsConstructor
@NoArgsConstructor
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    private String imageUrl;

    private Integer quantity;

    @Enumerated(value = EnumType.STRING)
    private Category category;

    @Enumerated
    private ItemStatus itemStatus;
}
