package com.couchpod.api.streams;

/**
 * Persistence Entity <-> DTO Mapper
 * implemented using explicit manual model creation.
 */
public class StreamMapper {

    public static StreamEntity fromDTO(CreateStreamRequestDTO dto) {
        return new StreamEntity() {{
            this.name = dto.streamName;
            this.description = dto.description;
            this.visibility = dto.visibility;
        }};
    }

    public static StreamDTO toDTO(StreamEntity entity) {
        return new StreamDTO() {{
            this.name = entity.name;
        }};
    }
}
