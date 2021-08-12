# TMDB Lite

Android application that allow the users to see a list of the Popular TV Shows with its details. It also support basic search and mark them as favorites/subscription.

Motivation
----------
This is a sample application built with latest arch and tools currently available in the community. It's a template with basic use cases for a 2021 Android Application.

Architecture
------------
It's based on Clean Architecture. Using a MVVM with Repository, and without Interactors (Use Cases). They would be useless here, but it can make sense if an interaction between data from different repositories needs to be done in one single scenario.

Libraries included
-----------------
- Kotlin
- Kotlin Coroutines/Flow
- Navigation Component
- Paging Library
- Palette
- Constraint and Motion Layout
- Hilt
- Retrofit and OkHttp
- Glide

CI
-------
There's already a workflow available for downloading the last stable build at https://github.com/feragusper/TMDBLite/actions

Support
-------
If you've found an error in this project, please file an issue: https://github.com/feragusper/TMDBLite/issues

Patches are encouraged, and may be submitted by forking this project and submitting a pull request through GitHub.

Contribute
----------
Pull requests are welcome.

1. Fork it!
2. Create your feature branch: `git checkout -b my-new-feature`
3. Commit your changes: `git commit -am 'Add some feature'`
4. Push to the branch: `git push origin my-new-feature`
5. Submit a pull request :D
