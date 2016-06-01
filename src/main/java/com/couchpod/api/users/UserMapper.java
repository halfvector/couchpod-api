package com.couchpod.api.users;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;

/**
 * Persistence Entity <-> DTO Mapper
 * implemented using ModelMapper.
 */
public class UserMapper {
    private ModelMapper modelMapper = new ModelMapper();

    public class UserRegistrationToEntity extends PropertyMap<UserRegistrationRequestDTO, UserEntity> {
        protected void configure() {
            map(source.fullName, destination.fullName);
            map(source.email, destination.email);
        }
    }

    public class UserEntityToDTO extends PropertyMap<UserEntity, UserDTO> {
        protected void configure() {
            map(source.fullName, destination.fullName);
            map(source.userId, destination.userId);
        }
    }

    public UserMapper() {
        modelMapper.addMappings(new UserRegistrationToEntity());
        modelMapper.addMappings(new UserEntityToDTO());
    }

    public ModelMapper getModelMapper() {
        return modelMapper;
    }
}
