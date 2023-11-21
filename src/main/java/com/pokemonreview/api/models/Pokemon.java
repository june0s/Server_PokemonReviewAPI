package com.pokemonreview.api.models;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@DynamicUpdate // 수정하겠다고 들어온 값만 업데이트. 빈 값은 null 설정 안 함
public class Pokemon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pokemon_id")
    private int id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    private PokemonType type;

    // mappedBy: foreign key 적어준다. orphanRemoval=true : 특정 포켓몬 삭제하면 review 도 다 삭제하겠다.
    @OneToMany(mappedBy = "pokemon", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<Review>();
}
