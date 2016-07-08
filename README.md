# Overview

The purpose of this library is to aid with conversions from strings to the proper Kafka Connect data types. Hopefully this library can be usedful in reducing the amount of mind numbing string parsing code.

# Type Parsing

This section of code will help with conversions from strings to the proper Kafka Connect data types. Hopefully this library can be useful in reducing the amount of mind numbing string parsing code used
transform Strings to Integers. The current code supports nullable schemas. An exception will be thrown if a null is passed with a schema that is not optional.

## Convert to a schema type

```
Parser parser = new Parser();
Object value = parser.parseString(Schema.BOOLEAN_SCHEMA, "true");
```

```
Parser parser = new Parser();
Object value = parser.parseJson(Schema.BOOLEAN_SCHEMA, objectMapper.valueToTree(true));
```

## Nulls are supported too

```
Parser parser = new Parser();
Object value = parser.parseString(Schema.OPTIONAL_BOOLEAN_SCHEMA, null);
```

```
Parser parser = new Parser();
Object value = parser.parseJson(Schema.OPTIONAL_BOOLEAN_SCHEMA, null);
```

## Schemas without optional set do not support nulls

If you pass in a null to a schema that is not optional, you're going to have a bad time.

```
Parser parser = new Parser();
Object value = parser.parseString(Schema.BOOLEAN_SCHEMA, null);
```

```
Parser parser = new Parser();
Object value = parser.parseJson(Schema.BOOLEAN_SCHEMA, null);
```

## Register your own type converter or an alternate date format 

```
Parser parser = new Parser();
parser.registerTypeParser(Timestamp.SCHEMA, new DateTypeConverter(TimeZone.getTimeZone("UTC"), new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss")));
```