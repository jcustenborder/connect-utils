# Overview

The purpose of this library is to aid with conversions from strings to the proper Kafka Connect data types. Hopefully this library can be usedful in reducing the amount of mind numbing string parsing code.

# Examples

## Convert to a schema type

```
StringParser converter = new StringParser();
Object value = parse.parseString(Schema.BOOLEAN_SCHEMA, "true");
```

## Nulls are supported too

```
Converter converter = new Converter();
Object value = converter.parseString(Schema.OPTIONAL_BOOLEAN_SCHEMA, "true");
```

## Optional schemas do not support nulls

```
Converter converter = new Converter();
Object value = converter.parseString(Schema.BOOLEAN_SCHEMA, null);
```

## Register your own type converter or an alternate date format 

```
Converter converter = new Converter();
converter.registerTypeConverter(Timestamp.SCHEMA, new DateTypeConverter(TimeZone.getTimeZone("UTC"), new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss")));
Object value = converter.parseString(Schema.BOOLEAN_SCHEMA, null);
```