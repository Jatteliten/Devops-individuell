# Pokemon Database
Spring boot website that shows a list of Pokémon and detailed descriptions of each if clicked on. With data fetched from PokéApi.

## About the project
This is a small Spring boot project for a course in DevOps from the Java developer program at Nackademin.

It allows you to populate a database with every Pokémon, typing and move from the PokéApi at https://pokeapi.co/.

This will be displayed in a list on the website, which allows you to click to a separate info page about each Pokémon.
The list can be filtered by Pokémon name via the search bar or Pokémon type by clicking any Pokémon type button present on the page.

The information page of each Pokémon will give you information about type weaknesses/strengths, which attacks it can learn
and some flavor text from the Pokédex entry.

Objects and attributes that will be saved to your database:

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
    - If your database is not populated, the program will do so automatically. This will take a while.
3. Run website
    - Simply run the program with a populated database to use the website.

## Authors
[Daniel Isaksson](https://github.com/Jatteliten/)

## Acknowledgments
**Teachers at Nackademin**

[Sigrun Olafsdottir](https://github.com/sigrunolafsdottir) - OOP, Database, Backend, etc.

[Mahmud Al Hakim](https://github.com/mahmudalhakim/) - Frontend, Design patterns.

[Stefan Holmberg](https://github.com/aspcodenet) - Backend, IT security, DevOps, etc.

**Api used in the project**

[PokéApi](https://pokeapi.co/)
