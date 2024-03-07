package com.example.musify.config;

import com.example.musify.auth.dto.response.LoginDto;
import com.example.musify.dto.response.*;
import com.example.musify.entity.*;
import com.example.musify.enumeration.ERole;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Configuration
public class ModelMapperConfig {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        TypeMap<Album, AlbumDto> albumTypeMap = modelMapper
                .createTypeMap(Album.class, AlbumDto.class);
        albumTypeMap.addMapping(src -> src.getArtist().getId(), AlbumDto::setArtistId);
        albumTypeMap.addMapping(src -> src.getArtist().getName(), AlbumDto::setArtistName);
        albumTypeMap.addMapping(src -> src.getArtist().getSlug(), AlbumDto::setArtistSlug);

        TypeMap<Album, ListAlbumDto> listAlbumTypeMap = modelMapper
                .createTypeMap(Album.class, ListAlbumDto.class);
        listAlbumTypeMap.addMapping(src -> src.getArtist().getName(), ListAlbumDto::setArtistName);
        listAlbumTypeMap.addMapping(src -> src.getArtist().getSlug(), ListAlbumDto::setArtistSlug);

        TypeMap<AlbumList, AlbumListDto> albumListTypeMap = modelMapper
                .createTypeMap(AlbumList.class, AlbumListDto.class);
        albumListTypeMap.addMapping(src -> src.getUser().getId(), AlbumListDto::setUserId);
        albumListTypeMap.addMapping(src -> src.getUser().getUsername(), AlbumListDto::setUsername);
        albumListTypeMap.addMapping(src -> src.getUser().getId(), AlbumListDto::setUserId);

        TypeMap<AlbumRating, AlbumRatingSummaryDto> albumRatingTypeMap = modelMapper
                .createTypeMap(AlbumRating.class, AlbumRatingSummaryDto.class);
        albumRatingTypeMap.addMapping(src -> src.getAlbum().getTitle(), AlbumRatingSummaryDto::setAlbumTitle);
        albumRatingTypeMap.addMapping(src -> src.getAlbum().getArtist().getName(), AlbumRatingSummaryDto::setArtistName);
        albumRatingTypeMap.addMapping(src -> src.getAlbum().getArtist().getSlug(), AlbumRatingSummaryDto::setArtistSlug);
        albumRatingTypeMap.addMapping(src -> src.getAlbum().getReleaseDate(), AlbumRatingSummaryDto::setReleaseDate);
        albumRatingTypeMap.addMapping(src -> src.getAlbum().getImage(), AlbumRatingSummaryDto::setAlbumImage);
        albumRatingTypeMap.addMapping(src -> src.getAlbum().getSlug(), AlbumRatingSummaryDto::setAlbumSlug);

        TypeMap<AlbumRating, AlbumRatingCollectionDto> albumRatingCollectionTypeMap = modelMapper
                .createTypeMap(AlbumRating.class, AlbumRatingCollectionDto.class);
        albumRatingCollectionTypeMap.addMapping(src -> src.getAlbum().getImage(), AlbumRatingCollectionDto::setAlbumImage);
        albumRatingCollectionTypeMap.addMapping(src -> src.getAlbum().getArtist().getName(), AlbumRatingCollectionDto::setArtistName);
        albumRatingCollectionTypeMap.addMapping(src -> src.getAlbum().getArtist().getSlug(), AlbumRatingCollectionDto::setArtistSlug);
        albumRatingCollectionTypeMap.addMapping(src -> src.getAlbum().getReleaseDate(), AlbumRatingCollectionDto::setReleaseDate);
        albumRatingCollectionTypeMap.addMapping(src -> src.getAlbum().getTitle(), AlbumRatingCollectionDto::setAlbumTitle);
        albumRatingCollectionTypeMap.addMapping(src -> src.getAlbum().getSlug(), AlbumRatingCollectionDto::setAlbumSlug);
        albumRatingCollectionTypeMap.addMapping(AlbumRating::getCreatedAt, AlbumRatingCollectionDto::setRatedDate);

        TypeMap<AlbumRating, RecentAlbumRatingDto> recentAlbumRatingTypeMap = modelMapper
                .createTypeMap(AlbumRating.class, RecentAlbumRatingDto.class);
        recentAlbumRatingTypeMap.addMapping(src -> src.getAlbum().getArtist().getName(), RecentAlbumRatingDto::setArtistName);
        recentAlbumRatingTypeMap.addMapping(src -> src.getAlbum().getTitle(), RecentAlbumRatingDto::setAlbumTitle);
        recentAlbumRatingTypeMap.addMapping(src -> src.getAlbum().getImage(), RecentAlbumRatingDto::setAlbumImage);
        recentAlbumRatingTypeMap.addMapping(src -> src.getAlbum().getSlug(), RecentAlbumRatingDto::setAlbumSlug);
        recentAlbumRatingTypeMap.addMapping(src -> src.getAlbum().getArtist().getSlug(), RecentAlbumRatingDto::setArtistSlug);
        recentAlbumRatingTypeMap.addMapping(src -> src.getAlbum().getReleaseDate(), RecentAlbumRatingDto::setReleaseDate);
        recentAlbumRatingTypeMap.addMapping(src -> src.getUser().getUsername(), RecentAlbumRatingDto::setUsername);

        TypeMap<Review, ReviewDto> reviewTypeMap = modelMapper
                .createTypeMap(Review.class, ReviewDto.class);
        reviewTypeMap.addMapping(src -> src.getUser().getId(), ReviewDto::setUserId);
        reviewTypeMap.addMapping(src -> src.getUser().getUsername(), ReviewDto::setUsername);
        reviewTypeMap.addMapping(src -> src.getUser().getImageUrl(), ReviewDto::setUserImage);

        TypeMap<User, UserDto> userTypeMap = modelMapper
                .createTypeMap(User.class, UserDto.class);
        userTypeMap.addMappings(mapping -> mapping.using(new RolesToStringsConverter())
                .map(User::getRoles, UserDto::setRoles));
//
//        TypeMap<User, LoginDto> loginUserTypeMap = modelMapper
//                .createTypeMap(User.class, LoginDto.class);
//        userTypeMap.addMappings(mapping -> mapping.using(new RolesToStringsConverter())
//                .map(User::getRoles, UserDto::setRoles));

        return modelMapper;
    }

    public static class RolesToStringsConverter extends AbstractConverter<Set<Role>, List<ERole>> {
        protected List<ERole> convert(Set<Role> roles) {
            return roles.stream()
                    .map(Role::getName)
                    .collect(Collectors.toList());
        }
    }
}
