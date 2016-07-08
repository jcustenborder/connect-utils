# Overview

The purpose of this library is to aid with conversions from strings to the proper Kafka Connect data types. Hopefully this library can be usedful in reducing the amount of mind numbing string parsing code.

# Type Parsing

This section of code will help with conversions from strings to the proper Kafka Connect data types. Hopefully this library can be useful in reducing the amount of mind numbing string parsing code used
transform Strings to Integers. The current code supports nullable schemas. An exception will be thrown if a null is passed with a schema that is not optional.

## Convert to a schema type

```
StringParser parser = new StringParser();
Object value = parser.parseString(Schema.BOOLEAN_SCHEMA, "true");
```

## Nulls are supported too

```
StringParser parser = new StringParser();
Object value = parser.parseString(Schema.OPTIONAL_BOOLEAN_SCHEMA, "true");
```

## Optional schemas do not support nulls

```
StringParser parser = new StringParser();
Object value = parser.parseString(Schema.BOOLEAN_SCHEMA, null);
```

## Register your own type converter or an alternate date format 

```
StringParser parser = new StringParser();
parser.registerTypeParser(Timestamp.SCHEMA, new DateTypeConverter(TimeZone.getTimeZone("UTC"), new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss")));
Object value = parser.parseString(Schema.BOOLEAN_SCHEMA, null);
```