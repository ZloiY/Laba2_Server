/**
 * Autogenerated by Thrift Compiler (0.10.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.company.thrift;


import java.util.Map;
import java.util.HashMap;
import org.apache.thrift.TEnum;

public enum PatternGroup implements org.apache.thrift.TEnum {
  MV_PATTERNS(1),
  STRUCT_PATTERNS(2),
  CREAT_PATTERNS(3),
  BEHAVE_PATTERNS(4);

  private final int value;

  private PatternGroup(int value) {
    this.value = value;
  }

  /**
   * Get the integer value of this enum value, as defined in the Thrift IDL.
   */
  public int getValue() {
    return value;
  }

  /**
   * Find a the enum type by its integer value, as defined in the Thrift IDL.
   * @return null if the value is not found.
   */
  public static PatternGroup findByValue(int value) { 
    switch (value) {
      case 1:
        return MV_PATTERNS;
      case 2:
        return STRUCT_PATTERNS;
      case 3:
        return CREAT_PATTERNS;
      case 4:
        return BEHAVE_PATTERNS;
      default:
        return null;
    }
  }
}
