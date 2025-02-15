package wwon.seokk.abandonedpets.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import wwon.seokk.abandonedpets.R
import wwon.seokk.abandonedpets.domain.entity.abandonmentpublic.AbandonmentPublicResultEntity
import wwon.seokk.abandonedpets.ui.common.FavoriteButton
import wwon.seokk.abandonedpets.ui.common.PetNoticeSurface
import wwon.seokk.abandonedpets.ui.theme.AbandonedPetsTheme
import wwon.seokk.abandonedpets.util.calculateAge

/**
 * Created by WonSeok on 2022.08.15
 **/
@Composable
fun PetCard(
    pet: AbandonmentPublicResultEntity,
    isLiked: Boolean,
    color: Color = AbandonedPetsTheme.colors.surfaceColor,
    favoriteClick: (AbandonmentPublicResultEntity, MutableState<Boolean>) -> Unit,
    petClick: (AbandonmentPublicResultEntity) -> Unit
) {
    Card(
        elevation = 0.dp,
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
            .height(145.dp)
            .clickable(
                enabled = true,
                onClick = {
                    petClick.invoke(pet)
                }),
        backgroundColor = color
    ) {
        Row {
            PetImage(pet)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp, 5.dp, 5.dp, 5.dp)
            ) {
                PetNoticeSurface(pet)
                Column(
                    modifier = Modifier.padding(top = 10.dp, end = 19.dp),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.Start
                ) {
                    PetInfo(pet)
                    PetInfo2(pet)
                    PetShelter(pet)
                    PetPlace(pet)
                }
                val state = mutableStateOf(isLiked)
                FavoriteButton(
                    isLiked = isLiked,
                    modifier = Modifier
                        .width(24.dp)
                        .align(Alignment.End),
                    state = state) {
                    favoriteClick.invoke(pet, state)
                }
            }
        }
    }
}

@Composable
private fun PetInfo(pet: AbandonmentPublicResultEntity) {
    val petKind = if(pet.kindCd.isNotBlank()) "${pet.kindCd} · " else ""
    Text(text = "$petKind${pet.colorCd}",
        style = AbandonedPetsTheme.typography.body1,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
private fun PetInfo2(pet: AbandonmentPublicResultEntity) {
    val petSex = if(pet.sexCd.isNotBlank()) {
        when(pet.sexCd) {
            "M" -> stringResource(id = R.string.common_male)
            "F" -> stringResource(id = R.string.common_female)
            else -> stringResource(id = R.string.common_unknown)
        }.plus(" · ")
    } else ""
    val birthYear = pet.age.replace(stringResource(id = R.string.common_age_year), "")
    val petAge = calculateAge(birthYear)
    Text(text = stringResource(id = R.string.format_sex_age, petSex, petAge, birthYear),
        style = AbandonedPetsTheme.typography.body1,
        modifier = Modifier.padding(bottom = 3.dp),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
private fun PetShelter(pet: AbandonmentPublicResultEntity) {
    Text(text = pet.careNm,
        style = AbandonedPetsTheme.typography.body2.copy(color = Color.Gray),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
private fun PetPlace(pet: AbandonmentPublicResultEntity) {
    Text(text = pet.happenPlace,
        fontWeight = FontWeight.Bold,
        style = AbandonedPetsTheme.typography.body1,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
private fun PetImage(pet: AbandonmentPublicResultEntity) {
    Card(
        modifier = Modifier
            .padding(5.dp)
            .width(135.dp)
            .height(135.dp)
            .clip(AbandonedPetsTheme.shapes.largeRoundCornerShape)
    ) {
        AsyncImage(model = ImageRequest.Builder(LocalContext.current)
            .data(pet.popfile)
            .crossfade(true)
            .build(),
            contentScale = ContentScale.FillBounds,
            placeholder = painterResource(R.drawable.ic_spoor),
            contentDescription = stringResource(id = R.string.pet_image_description),
            modifier = Modifier.fillMaxWidth()
        )
    }
}


@Preview(showBackground = true)
@Composable
private fun PetCardPreview() {
    AbandonedPetsTheme{
        PetCard(AbandonmentPublicResultEntity.EMPTY,false, Color.White, favoriteClick = { _, _ -> }, petClick = {} )
    }
}