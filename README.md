# AntMonitorExample
This is a repository for a simple example of how to use the [AntMonitor
Library](https://github.com/UCI-Networking-Group/AntMonitor).

## Using the AntMonitor Library in Your Own App
### Setup
* The library is available in this repo: `app/libs/antmonitorlib.aar`.
Download it from GitHub as a raw file.
* Place the .arr file into the 'libs' folder of your project
(e.g. `YourAndroidProject/app/libs/antmonitorlib.aar`)
* Configure your build.gradle file to use the 'libs' folder:

```
repositories {
    flatDir {
        dirs 'libs'
    }
}
```

* Declare the library as one of your dependencies:
```
dependencies {
    ...
    compile(name:'antmonitorlib', ext:'aar')
}
```
You are now ready to use the AntMonitor Library!

### Examples
You can either use the app provided in this repo as a starting point,
or follow a step-by-step guide available
[here](https://uci-networking-group.github.io/AntMonitorExample).

### Citing AntMonitor
If you create a publication (including web pages, papers published by a
third party, and publicly available presentations) using the AntMonitor
app or the AntMonitor Library, please cite the corresponding paper as
follows:

```
@article{shuba2016antmonitor,
  title={AntMonitor: A System for On-Device Mobile Network Monitoring and its Applications},
  author={Shuba, Anastasia and Le, Anh and Alimpertis, Emmanouil and Gjoka, Minas and Markopoulou, Athina},
  journal={arXiv preprint arXiv:1611.04268},
  year={2016}
}
```