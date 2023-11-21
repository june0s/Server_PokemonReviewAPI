package com.pokemonreview.api.service.impl;

import com.pokemonreview.api.dto.PageResponse;
import com.pokemonreview.api.dto.PokemonDto;
import com.pokemonreview.api.exceptions.ResourceNotFoundException;
import com.pokemonreview.api.models.Pokemon;
import com.pokemonreview.api.repository.PokemonRepository;
import com.pokemonreview.api.service.PokemonService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor // final 인 객체의 생성자를 만들어주는 anotation
public class PokemonServiceImpl implements PokemonService {
    private final PokemonRepository pokemonRepository;
    private final ModelMapper modelMapper;

    // Constructor injection(생성자 주입, mock 객체 주입하기 위해서) @RequiredArgsConstructor 추가하면 아래처럼 기술 안해도 됨.
//    public PokemonServiceImpl(PokemonRepository pokemonRepository) {
//        this.pokemonRepository = pokemonRepository;
//    }

    @Override
    public PokemonDto createPokemon(PokemonDto pokemonDto) {
//        Pokemon pokemon = mapToEntity(pokemonDto);
        Pokemon pokemon = modelMapper.map(pokemonDto, Pokemon.class);

        Pokemon newPokemon = pokemonRepository.save(pokemon);

//        PokemonDto pokemonResponse = new PokemonDto();
//        pokemonResponse.setId(newPokemon.getId());
//        pokemonResponse.setName(newPokemon.getName());
//        pokemonResponse.setType(newPokemon.getType());
//        return mapToDto(newPokemon);
        return modelMapper.map(newPokemon, PokemonDto.class);
    }

    @Override
    public PageResponse<?> getAllPokemon(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by("id").descending());

        Page<Pokemon> pokemonPage = pokemonRepository.findAll(pageable);

        List<Pokemon> listOfPokemon = pokemonPage.getContent();
        List<PokemonDto> content = listOfPokemon
                .stream() //Stream<Pokemon>
//                .map(p -> mapToDto(p)) //Stream<PokemonDto>
                .map(this::mapToDto)
                .collect(Collectors.toList()); //List<PokemonDto>

        PageResponse<PokemonDto> pokemonResponse = new PageResponse<>();
        pokemonResponse.setContent(content);
        pokemonResponse.setPageNo(pokemonPage.getNumber());
        pokemonResponse.setPageSize(pokemonPage.getSize());
        pokemonResponse.setTotalElements(pokemonPage.getTotalElements());
        pokemonResponse.setTotalPages(pokemonPage.getTotalPages());
        pokemonResponse.setLast(pokemonPage.isLast());

        return pokemonResponse;
    }

    @Override
    public PokemonDto getPokemonById(int id) {
        Pokemon pokemon = getExistPokemon(id);
        return mapToDto(pokemon);
    }

    private Pokemon getExistPokemon(int id) {
        return pokemonRepository
                .findById(id) // Optional<Pokemon>
                .orElseThrow(() ->
                        new ResourceNotFoundException("Pokemon could not be found"));
    }

    @Override
    public PokemonDto updatePokemon(PokemonDto pokemonDto, int id) {
        Pokemon pokemon = getExistPokemon(id);

        // Entity 의 setter method 호출
        if (pokemonDto.getName() != null) {
            pokemon.setName(pokemonDto.getName());
        }
        if (pokemonDto.getType() != null) {
            pokemon.setType(pokemonDto.getType());
        }

        // save 안 해도 @Transactional 이 걸려 있기 때문에. 영속성. update query 가 실행된다. (dirty checking)
        //Pokemon updatedPokemon = pokemonRepository.save(pokemon);
        return mapToDto(pokemon);
    }

    @Override
    public void deletePokemonId(int id) {
        Pokemon pokemon = getExistPokemon(id);
        pokemonRepository.delete(pokemon);
    }

    // Entity -> Dto
    private PokemonDto mapToDto(Pokemon pokemon) {
        PokemonDto pokemonDto = new PokemonDto();
        pokemonDto.setId(pokemon.getId());
        pokemonDto.setName(pokemon.getName());
        pokemonDto.setType(pokemon.getType());
        return pokemonDto;
    }

    // Dto -> Entity
    private Pokemon mapToEntity(PokemonDto pokemonDto) {
        Pokemon pokemon = new Pokemon();
        pokemon.setName(pokemonDto.getName());
        pokemon.setType(pokemonDto.getType());
        return pokemon;
    }
}
