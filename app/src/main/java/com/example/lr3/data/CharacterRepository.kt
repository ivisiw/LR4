package com.example.lr3.data

import com.example.lr3.data.local.FavouriteDao
import com.example.lr3.data.local.FavouriteEntity
import com.example.lr3.data.remote.CharacterApi
import com.example.lr3.data.remote.toDomain
import com.example.lr3.model.Character
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

data class CharactersResult(
    val characters: List<Character>,
    val hasNextPage: Boolean
)

@Singleton
class CharacterRepository @Inject constructor(
    private val api: CharacterApi,
    private val favouriteDao: FavouriteDao
) {
    private var cachedFavouriteIds: Set<Int> = emptySet()

    suspend fun searchCharacters(
        query: String,
        page: Int,
        status: String = ""
    ): CharactersResult {
        if (page == 1) {
            cachedFavouriteIds = favouriteDao.getAll().map { it.id }.toSet()
        }
        return try {
            val response = api.getCharacters(query, page, status)
            CharactersResult(
                characters = response.results.map { dto ->
                    dto.toDomain(isFavourite = dto.id in cachedFavouriteIds)
                },
                hasNextPage = response.info.next != null
            )
        } catch (e: HttpException) {
            if (e.code() == 404) CharactersResult(emptyList(), false) else throw e
        }
    }

    suspend fun getCharacter(id: Int): Character {
        val isFav = favouriteDao.isFavourite(id)
        return api.getCharacterById(id).toDomain(isFavourite = isFav)
    }

    suspend fun getFavourites(): List<Character> {
        return favouriteDao.getAll().map { entity ->
            Character(
                id = entity.id,
                name = entity.name,
                status = entity.status,
                species = entity.species,
                type = entity.type,
                gender = entity.gender,
                originName = entity.originName,
                locationName = entity.locationName,
                episodeCount = entity.episodeCount,
                created = entity.created,
                isFavourite = true
            )
        }
    }

    suspend fun isFavourite(id: Int): Boolean {
        return favouriteDao.isFavourite(id)
    }

    suspend fun addFavourite(character: Character) {
        favouriteDao.insert(
            FavouriteEntity(
                id = character.id,
                name = character.name,
                status = character.status,
                species = character.species,
                type = character.type,
                gender = character.gender,
                originName = character.originName,
                locationName = character.locationName,
                episodeCount = character.episodeCount,
                created = character.created
            )
        )
        cachedFavouriteIds = cachedFavouriteIds + character.id
    }

    suspend fun removeFavourite(id: Int) {
        favouriteDao.deleteById(id)
        cachedFavouriteIds = cachedFavouriteIds - id
    }
}