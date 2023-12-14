package sk.stuba.fei.uim.vsa.pr2.web.factory;

import sk.stuba.fei.uim.vsa.pr2.entities.Holiday;
import sk.stuba.fei.uim.vsa.pr2.web.response.HolidayDTO;

public class HolidayResponseFactory implements ResponseFactory<Holiday, HolidayDTO> {
    @Override
    public HolidayDTO transformToDto(Holiday entity) {
        HolidayDTO dto = new HolidayDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDate(entity.getDate());
        return dto;
    }

    @Override
    public Holiday transformToEntity(HolidayDTO dto) {
        Holiday holiday = new Holiday();
        holiday.setId(dto.getId());
        holiday.setName(dto.getName());
        holiday.setDate(dto.getDate());
        return holiday;
    }
}
