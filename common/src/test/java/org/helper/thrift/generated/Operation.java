/**
 * Autogenerated by Thrift Compiler (0.11.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package org.helper.thrift.generated;


public enum Operation implements org.apache.thrift.TEnum {
  LOGIN(1),
  REGISTER(2);

  private final int value;

  private Operation(int value) {
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
  public static Operation findByValue(int value) { 
    switch (value) {
      case 1:
        return LOGIN;
      case 2:
        return REGISTER;
      default:
        return null;
    }
  }
}
