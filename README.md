# sodata-flatgeobuf

- todo: Testen http get range: https://developer.mozilla.org/en-US/docs/Web/HTTP/Range_requests

## Beschreibung

## Komponenten

## Konfigurieren und Starten

## Externe Abhängigkeiten

GDAL...

## Interne Struktur

## Entwicklung

### Prepare

```
multipass launch jammy --cpus 4 --disk 20G --memory 8G --name sodata-fgb
multipass mount $HOME/sources sodata-fgb:/home/ubuntu/sources
multipass mount $HOME/tmp sodata-fgb:/home/ubuntu/tmp
multipass shell sodata-fgb
multipass stop sodata-fgb
```

```
sudo apt-get -y install zip unzip
curl -s "https://get.sdkman.io" | bash
source "/home/ubuntu/.sdkman/bin/sdkman-init.sh"
sdk i java 22.3.r17-grl
```

```
sudo apt-get update
sudo apt-get -y install gdal-bin
```

```

```

### Run

```
export S3_ACCESS_KEY=xxxx
export S3_SECRET_KEY=yyyy
```

```
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

### Build


