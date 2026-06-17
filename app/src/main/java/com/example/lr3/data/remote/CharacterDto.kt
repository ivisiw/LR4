package com.example.lr3.data.remote

import com.example.lr3.model.Character

data class PageInfo(
    val next: String?
)

data class CharacterResponse(
    val info: PageInfo,
    val results: List<CharacterDto>
)

data class LocationDto(
    val name: String
)

data class CharacterDto(
    val id: Int,
    val name: String,
    val status: String,
    val species: String,
    val type: String,
    val gender: String,
    val origin: LocationDto,
    val location: LocationDto,
    val episode: List<String>,
    val created: String
)

fun CharacterDto.toDomain(isFavourite: Boolean = false): Character {
    return Character(
        id = id,
        name = name,
        status = status,
        species = species,
        type = type,
        gender = gender,
        originName = origin.name,
        locationName = location.name,
        episodeCount = episode.size,
        created = created.take(10),
        isFavourite = isFavourite
    )
}