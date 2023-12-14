package sk.stuba.fei.uim.vsa.pr2.web.factory;

import sk.stuba.fei.uim.vsa.pr2.entities.CPF_ID;
import sk.stuba.fei.uim.vsa.pr2.web.response.CPF_ID_DTO;

public class CPF_ID_DtoResponseFactory implements ResponseFactory<CPF_ID, CPF_ID_DTO> {
    @Override
    public CPF_ID_DTO transformToDto(CPF_ID entity) {
        CPF_ID_DTO dto = new CPF_ID_DTO();
        dto.setCarParkId(entity.getCarParkId());
        dto.setFloorIdentifier(entity.getFloorIdentifier());
        return dto;
    }

    @Override
    public CPF_ID transformToEntity(CPF_ID_DTO dto) {
        CPF_ID cpf_id=new CPF_ID();
        cpf_id.setCarParkId(dto.getCarParkId());
        cpf_id.setFloorIdentifier(dto.getFloorIdentifier());
        return cpf_id;
    }
}
