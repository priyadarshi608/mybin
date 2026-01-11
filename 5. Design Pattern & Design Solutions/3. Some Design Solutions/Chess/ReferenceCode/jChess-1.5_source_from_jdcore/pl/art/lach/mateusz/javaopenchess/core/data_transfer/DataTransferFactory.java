package pl.art.lach.mateusz.javaopenchess.core.data_transfer;

import pl.art.lach.mateusz.javaopenchess.core.data_transfer.implementations.FenNotation;
import pl.art.lach.mateusz.javaopenchess.core.data_transfer.implementations.PGNNotation;


















public class DataTransferFactory
{
  public DataTransferFactory() {}
  
  public static DataExporter getExporterInstance(TransferFormat format)
  {
    switch (1.$SwitchMap$pl$art$lach$mateusz$javaopenchess$core$data_transfer$TransferFormat[format.ordinal()])
    {
    case 1: 
      return new FenNotation();
    case 2: 
      return new PGNNotation();
    }
    return new FenNotation();
  }
  

  public static DataImporter getImporterInstance(TransferFormat format)
  {
    switch (1.$SwitchMap$pl$art$lach$mateusz$javaopenchess$core$data_transfer$TransferFormat[format.ordinal()])
    {
    case 1: 
      return new FenNotation();
    case 2: 
      return new PGNNotation();
    }
    return new FenNotation();
  }
}
