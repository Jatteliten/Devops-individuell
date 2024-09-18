# Pokemon Database
Spring boot website that shows a list of Pokémon and detailed descriptions of each if clicked on. With data fetched from PokéApi.

## About the project
This is a small Spring boot project for a course in DevOps from the Java developer program at Nackademin.

It allows you to populate a database with every Pokémon, typing and move from the PokéApi at https://pokeapi.co/.

This will be displayed in a list on the website, which allows you to click to a separate info page about each Pokémon.
The list can be filtered by Pokémon name via the search bar or Pokémon type by clicking any Pokémon type button present on the page.

Attributes that will be saved to your database:

```
Type 
   - Name
Move
   - Name
   - Damage class
Pokémon
   - Pokédex ID
   - Name
   - Flavor text
   - Link to sprite image
   - Link to full artwork
   - (List) Types
   - (List) Moves
```

## Getting started

1. Set up database access
    -  Set up your env-file to connect to a database of your choice.
2. Fetch data
    - Run the application with the argument "fetch-data" to fetch all the necessary data from PokéApi and build the database.
        * The program will tell you when it is done populating the database in the console.
3. Run website
    - Run the program without arguments to use the website.

## Authors
[Daniel Isaksson](https://github.com/Jatteliten/)

## Acknowledgments
**Teachers at Nackademin**

[Sigrun Olafsdottir](https://github.com/sigrunolafsdottir) - OOP, Database, Backend, etc.

[Stefan Holmberg](https://github.com/aspcodenet) - Backend, IT security, DevOps, etc.

[Mahmud Al Hakim](https://github.com/mahmudalhakim/) - Frontend, Design patterns.

**Api used in the project**
[PokéApi](https://pokeapi.co/)
