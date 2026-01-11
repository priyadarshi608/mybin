package pl.art.lach.mateusz.javaopenchess.core.data_transfer;

import pl.art.lach.mateusz.javaopenchess.core.Game;
import pl.art.lach.mateusz.javaopenchess.core.exceptions.ReadGameError;

public abstract interface DataImporter
{
  public abstract Game importData(String paramString)
    throws ReadGameError;
  
  public abstract void importData(String paramString, Game paramGame)
    throws ReadGameError;
}
