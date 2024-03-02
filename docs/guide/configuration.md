In scala configuration files are usually using [HOCON format](https://github.com/lightbend/config/blob/main/HOCON.md).

## HOCON syntax basics

### Nesting
```
db.default.url = jdbc:postgresql://localhost:5433
db.default.user = root
db.default.password = 123123
```
Is the same as
```
db {
  default {
    url = jdbc:postgresql://localhost:5433
    user = root
    password = 123123
  }
}
```
Is the same as
```
db.default {
  url = jdbc:postgresql://localhost:5433
  user = root
  password = 123123
}
```
Is the same as
```
db.default {
  url = jdbc:postgresql://localhost:5433
  user = root
}
db.default.password = 123123
```
### Sequential overrides

One key can be used multiple times, output will the the last value. For example:
```
# shared.conf
aaa = aaa-shared
bbb = bbb-shared
ccc = ccc-shared

# secret.conf
ccc = ccc-secret

# application.conf
include "shared.conf"
bbb = bbb-application
ccc = ccc-application
include "secret.conf"
```

The result from loading `application.conf` will be
```
aaa = aaa-shared
bbb = bbb-application
ccc = ccc-secret
```

### Includes
```
# include from resources, ignore if not found (it can fail later in the app on missing key)
include "environments/shared.conf"

# include from resources, if not found fail immediatelly
include required("environments/shared.conf")

# include from file on fs, ignore if not found (it can fail later in the app on missing key)
include file("environments/shared.conf")

# include from file on fs, if not found fail immediatelly
include required(file("environments/shared.conf"))
```

### Substitutions
```
animal.favorite = Dog
key = ${animal.favorite} is my favorite animal
```
The resulting value for the `key` will be `Dog is my favorite animal`. If the value is missing it will fail.

If we do not want to fail on missing value we can use
```
key = ${?animal.favorite} is my favorite animal
```

It is useful when working with environment variables and if it is not set, we don't want to fail but use default value
```
db.password = "default-password"
# this will not fail when DB_PASS is not set and will keep password value
db.password = ${?DB_PASS}
```
