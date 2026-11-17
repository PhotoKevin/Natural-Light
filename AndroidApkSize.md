# Introduction
Beginning in Feb 2027 Google will penalize apps that are larger than 10MB. The data for TimeShape is 23MB pushing
your app past the threshold. To avoid the penalties, you should exclude the data from your app bundle and
download it later.

- Exclude the data from the apk
- Get the data file for your version of TimeShape
- Create a server to supply the data at runtime
- Modify your initialization code

# Exclude the data
In your app build.gradle.kts add this to the android section

```agsl
android {
    ....

    packaging {
        resources {
            excludes += "data.tar.zstd"
        }
    }
}
```
# Get the appropriate data
The repository on GitHub contains the scripts to create the datafile, but 
it's probably easier to extract it from the jar. On Windows you will
find it somewhere under your user directory. 

```agsl
C:\Users\kevin>dir timeshape-2026b*.jar /s
```
Gives quite a few copies of it. Realizing that a JAR file is just a ZIP file
you can use the usual tools to extract it.

# Creating a server
At a bare minimum you can simply download the file from a known location.

# Modify initialization

There are several versions of the initialize function that  take a TarArchiveInputStream.
This is the simplest.
```agsl
try (InputStream resourceAsStream = timeShapeDataFile.toURI ().toURL ().openStream ();
    ZstdInputStream unzipStream = new ZstdInputStream (resourceAsStream);
    BufferedInputStream bufferedStream = new BufferedInputStream (unzipStream);
    TarArchiveInputStream shapeInputStream = new TarArchiveInputStream (bufferedStream))
{
  timeZoneEngine = TimeZoneEngine.initialize (shapeInputStream);
}
catch (IOException ex)
{
  logger.error ("Can't initialize TimeZoneEngine: ", ex);
}

```

Beware, however, that the initialize function is hardcoded to expect a data file with
a particular number of time zones. If you supply a data file from some other
version it will likely have a different number of zones and your app will crash. 
A simple fix might be to rename data.tar.zstd to include the version number
so that your app can request the correct version. 

Another option is to use the initialize function that accepts the number of areas
as a parameter. 
```agsl
public static TimeZoneEngine initialize(Set<ZoneId> timeZones,
                                            boolean accelerateGeometry,
                                            int numberOfTimeZones,
                                            TarArchiveInputStream f) 
```
This will, however, require you to change you program to use a list of ZoneId
instead of a boundary box. If you go this route you will need to figure out
how many zones. One route would be to encode that in the file name:
```agsl
TimeShapeData-2026b-449.tar.zstd
```
This pretty much means  you can't use a simple path anymore and will need 
to make a server program. 

# Things to remember
You know all this but a checklist isn't a bad thing.
- Does your app have a fallback plan if the datafile cannot be downloaded? 
- Did you check for running on a metered connection and ask before doing the 
download?