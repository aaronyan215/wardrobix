# WardrobiX
Welcome to WardrobiX! This is a useful web app that allows users to easily generate smart, curated outfits with the simple click of a button. Import clothes from your IRL wardrobe to expand your closet, and WardrobiX will select a custom outfit for you based on your current location!

## Getting Started
Follow these instructions to download and run WardrobiX locally on any device.

### Prerequisites
- Git
- Docker

### Installing and Running
First clone the GitHub repository:
```bash
git clone https://github.com/aaronyan215/wardrobix.git
```

Now, in the cloned directory, load the docker images:
```bash
cd wardrobix
docker compose up
```

You should see that the app is now running! The backend runs on port 8080, but for practical use go to where the frontend is running:

'http://localhost:3000'

### Stopping the App
To stop and remove containers, networks, and volumes created by Docker Compose:
```bash
docker compose down -v
```

## Tech Stack
- **Backend:** Spring Boot, Spring Security, MySQL, WeatherAPI.com
- **Frontend:** React + Vite
- **Build-Tools:** Apache Maven, Docker (multi-arch)


## Usage
Once you run the app, you will be presented with an initial authentification page. If you have not made an account yet, simply click Sign Up
and create a username + password. You can then login with this info, and your closet/data will be stored in this account.

To add clothes to your wardrobe, just enter its name, primary color, and occasion. Additionally, you'll need to input its type: top, bottom, headwear, outerwear or footwear. Lastly, you must enter its subtype (more information later). To generate an outfit from your current closet, simply input the occasion and the city, and WardrobiX will gather the current weather and temperature of that city to curate an outfit for you!

## Outfit Generation Logic
Each clothing item in your closet is given a numeric score based on its compatibility with the following criteria: weather, temperature, color, and its cohesiveness with the rest of the current outfit. A lower score (minimum 0) means the clothing item is more compatible. The generated outfit is created with the highest scoring clothing items of each required category: top, bottom, and footwear. If an item belonging to the headwear or outerwear categories scores well enough, it ***may*** also be added to the outfit.

To avoid repetitiveness, the best scoring item is not ***always*** the one chosen for the outift. Instead, up to the top five highest scoring clothing items (including ties) are added to a list and chosen at random, with higher scoring items having a bigger weight/bias. 


### Subtypes
Each clothing type has unique subtypes:

- headwear
    - cap
    - beanie
- top
    - t-shirt
    - long-sleeve
    - hoodie
    - button-up
    - polo
- outerwear
    - jacket
    - puffer
    - flannel
    - vest
    - windbreaker
- bottom
    - athletic-shorts
    - sweat-shorts
    - jorts
    - cargo-shorts
    - jeans
    - sweatpants
    - cargo-pants
    - trousers
    - khakis
    - chinos
    - leggings
- footwear
    - sneakers
    - running-shoes
    - loafers
    - heels
    - sandals
    - slides
    - boots
    - winter-boots
