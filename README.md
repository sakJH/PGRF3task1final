# PGRF3task1final

##Body
 -  -/30

##Spuštění projektu
otevření projektu v IntelliJ IDEA (v2022.2.3)
 - SDK: openjdk19 (v19.0.1)
 - Language Level 19 (Preview)
vybrat složku res a shaders a označit jako Resourses Root
Right click -> Mark Direstory as -> Resources Root
přidání jwgl do struktury projektu
 - lwjgl-release-3.3.1
File -> Project Structure -> Global libraries -> + (New global library) -> Java -> (vybrat složku s jwgl - rozbalená složka)
File -> Project Structure -> Modules -> Dependencies -> + (Add) -> Library ... -> (vybrat jwgl) Add selected -> Apply
pokud v projektu něco svítí "červeně" je potřeba restartovat IDEU, aby se soubory znovu naindexovaly
