package tr.com.aktifbank.servicetester.data;

public interface IDataContainerHandler<TDataContainer> {

    TDataContainer getDataContainer();

    void setDataContainer(TDataContainer dataContainer);

}
