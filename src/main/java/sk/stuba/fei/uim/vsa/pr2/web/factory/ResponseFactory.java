package sk.stuba.fei.uim.vsa.pr2.web.factory;

public interface ResponseFactory<R, T> {

    T transformToDto(R entity);

    R transformToEntity(T dto);

}
