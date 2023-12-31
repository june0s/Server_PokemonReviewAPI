package com.pokemonreview.api.repository;

import com.pokemonreview.api.models.Pokemon;
import com.pokemonreview.api.models.PokemonType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PokemonRepository extends JpaRepository<Pokemon, Integer> {
    // select * from pokemon where type = ?
    // Optional 있을 수도 있고 없을 수도 있다.
    Optional<Pokemon> findByType(PokemonType type);
}
