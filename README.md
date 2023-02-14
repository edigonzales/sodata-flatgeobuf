# sodata-flatgeobuf

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
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

### Build




ogr2ogr -f FlatGeobuf foo.fgb  /vsizip/ch.so.afu.abbaustellen.gpkg.zip/ch.so.afu.abbaustellen.gpkg "abbaustelle"
ogr2ogr -f FlatGeobuf foo.fgb  ch.so.agi.av_gb_administrative_einteilungen.gpkg "grundbuchkreise_grundbuchkreis"

ogr2ogr -lco TEMPORARY_DIR=/tmp/ -f FlatGeobuf foo.fgb  ch.so.agi.av_gb_administrative_einteilungen.gpkg "grundbuchkreise_grundbuchkreis"