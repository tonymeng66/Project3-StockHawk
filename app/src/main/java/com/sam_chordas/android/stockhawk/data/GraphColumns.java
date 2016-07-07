package com.sam_chordas.android.stockhawk.data;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.ConflictResolutionType;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;
import net.simonvt.schematic.annotation.Unique;

/**
 * Created by sam_chordas on 10/5/15.
 */
public class GraphColumns {
  @DataType(DataType.Type.INTEGER)
  @PrimaryKey
  @AutoIncrement
  public static final String _ID = "_id";
  @DataType(DataType.Type.TEXT)
  @NotNull
  public static final String SYMBOL = "symbol";
  @DataType(DataType.Type.REAL)
  @NotNull
  public static final String BIDPRICE = "bid_price";
  @DataType(DataType.Type.TEXT)
  @NotNull
  public static final String DATE = "date";
  @DataType(DataType.Type.REAL)
  @NotNull
  public static final String VOLUME = "volume";
}

