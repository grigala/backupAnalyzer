# BackupAnalyzer

Getting to know what takes up space in your backup from a file listing taken from a backup created with https://www.borgbackup.org/

This is just a quick implementation to parse and vizualize the output of 

```
borg list  $BORG_REPO::backupname-2021-06-20T05:00:28 > backupsize.log 
cat backupsize.log  | awk '{print $4 " "  $8}' > size-filename.log
```

That way I got something similar to `ncdu` to realize what might be eating up my backup space ;-). It does not take into account the deduplication featuers of borg backup, tho.

## Screenshots
### Web version:
<img width="353" alt="Screenshot BackupAnalyzer" src="https://user-images.githubusercontent.com/37251/123536799-aca59e00-d72c-11eb-8d6e-39440ee9611d.png">

### JavaFX version:
<img alt="Screenshot BackupAnalyzerFX" src="https://user-images.githubusercontent.com/2084794/127773216-921b7fd4-7ec6-4d97-8f19-468c1b6d6b13.png">


## Development server

Run `ng serve` for a dev server. Navigate to `http://localhost:4200/`. The app will automatically reload if you change any of the source files.
