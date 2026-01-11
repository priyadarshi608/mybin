package pl.art.lach.mateusz.javaopenchess.core.data_transfer;

import pl.art.lach.mateusz.javaopenchess.core.Game;

public abstract interface DataExporter
{
  public abstract String exportData(Game paramGame);
}
