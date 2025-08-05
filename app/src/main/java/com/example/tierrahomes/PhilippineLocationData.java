package com.example.tierrahomes;

/**
 * PhilippineLocationData - Comprehensive location data structure for the Philippines
 * Based on PhilAtlas data (https://www.philatlas.com/municipalities.html)
 */
public class PhilippineLocationData {

    /**
     * Island groups in the Philippines
     */
    public static final String[] ISLAND_GROUPS = {
            "Luzon",
            "Visayas",
            "Mindanao"
    };

    /**
     * Mapping of Island Groups to their corresponding Regions
     */
    public static final String[][] ISLAND_TO_REGIONS = {
            // Luzon Regions
            {
                    "NCR",
                    "CAR",
                    "Ilocos Region",
                    "Cagayan Valley",
                    "Central Luzon",
                    "CALABARZON",
                    "MIMAROPA",
                    "Bicol Region"
            },
            // Visayas Regions
            {
                    "Western Visayas",
                    "Central Visayas",
                    "Eastern Visayas"
            },
            // Mindanao Regions
            {
                    "Zamboanga Peninsula",
                    "Northern Mindanao",
                    "Davao Region",
                    "SOCCSKSARGEN",
                    "Caraga",
                    "BARMM"
            }
    };

    /**
     * All regions with their provinces
     * Special case: NCR doesn't have provinces, it has districts
     */
    public static final LocationData LOCATION_DATA = new LocationData();

    /**
     * Class that holds all location data
     */
    public static class LocationData {
        // NCR (no provinces, directly has cities/municipalities)
        public final String[] NCR_CITIES = {
                "Caloocan City", "Las Piñas City", "Makati City", "Malabon City",
                "Mandaluyong City", "Manila City", "Marikina City", "Muntinlupa City",
                "Navotas City", "Parañaque City", "Pasay City", "Pasig City",
                "Quezon City", "San Juan City", "Taguig City", "Valenzuela City",
                "Pateros"
        };

        // CAR Provinces
        public final String[] CAR_PROVINCES = {
                "Abra", "Apayao", "Benguet", "Ifugao", "Kalinga", "Mountain Province"
        };

        // CAR Cities/Municipalities by province
        public final String[][] CAR_MUNICIPALITIES = {
                // Abra
                {
                        "Bangued", "Boliney", "Bucay", "Bucloc", "Daguioman", "Danglas",
                        "Dolores", "La Paz", "Lacub", "Lagangilang", "Lagayan", "Langiden",
                        "Licuan-Baay", "Luba", "Malibcong", "Manabo", "Peñarrubia", "Pidigan",
                        "Pilar", "Sallapadan", "San Isidro", "San Juan", "San Quintin", "Tayum",
                        "Tineg", "Tubo", "Villaviciosa"
                },
                // Apayao
                {
                        "Calanasan", "Conner", "Flora", "Kabugao", "Luna", "Pudtol", "Santa Marcela"
                },
                // Benguet
                {
                        "Baguio City", "Atok", "Bakun", "Bokod", "Buguias", "Itogon", "Kabayan",
                        "Kapangan", "Kibungan", "La Trinidad", "Mankayan", "Sablan", "Tuba", "Tublay"
                },
                // Ifugao
                {
                        "Aguinaldo", "Alfonso Lista", "Asipulo", "Banaue", "Hingyon", "Hungduan",
                        "Kiangan", "Lagawe", "Lamut", "Mayoyao", "Tinoc"
                },
                // Kalinga
                {
                        "Balbalan", "Lubuagan", "Pasil", "Pinukpuk", "Rizal", "Tabuk City", "Tanudan", "Tinglayan"
                },
                // Mountain Province
                {
                        "Barlig", "Bauko", "Besao", "Bontoc", "Natonin", "Paracelis", "Sabangan",
                        "Sadanga", "Sagada", "Tadian"
                }
        };

        // Region I (Ilocos) Provinces
        public final String[] ILOCOS_PROVINCES = {
                "Ilocos Norte", "Ilocos Sur", "La Union", "Pangasinan"
        };

        // Region I Cities/Municipalities by province
        public final String[][] ILOCOS_MUNICIPALITIES = {
                // Ilocos Norte
                {
                        "Laoag City", "Batac City", "Adams", "Bacarra", "Badoc", "Bangui", "Banna",
                        "Burgos", "Carasi", "Currimao", "Dingras", "Dumalneg", "Espiritu", "Marcos",
                        "Nueva Era", "Pagudpud", "Paoay", "Pasuquin", "Piddig", "Pinili", "San Nicolas",
                        "Sarrat", "Solsona", "Vintar"
                },
                // Ilocos Sur
                {
                        "Vigan City", "Candon City", "Alilem", "Banayoyo", "Bantay", "Burgos", "Cabugao",
                        "Caoayan", "Cervantes", "Galimuyod", "Gregorio del Pilar", "Lidlidda", "Magsingal",
                        "Nagbukel", "Narvacan", "Quirino", "Salcedo", "San Emilio", "San Esteban",
                        "San Ildefonso", "San Juan", "San Vicente", "Santa", "Santa Catalina",
                        "Santa Cruz", "Santa Lucia", "Santa Maria", "Santiago", "Santo Domingo",
                        "Sigay", "Sinait", "Sugpon", "Suyo", "Tagudin"
                },
                // La Union
                {
                        "San Fernando City", "Agoo", "Aringay", "Bacnotan", "Bagulin", "Balaoan",
                        "Bangar", "Bauang", "Burgos", "Caba", "Luna", "Naguilian", "Pugo",
                        "Rosario", "San Gabriel", "San Juan", "Santo Tomas", "Santol", "Sudipen", "Tubao"
                },
                // Pangasinan
                {
                        "Dagupan City", "Alaminos City", "San Carlos City", "Urdaneta City",
                        "Agno", "Aguilar", "Alcala", "Anda", "Asingan", "Balungao", "Bani",
                        "Basista", "Bautista", "Bayambang", "Binalonan", "Binmaley", "Bolinao",
                        "Bugallon", "Burgos", "Calasiao", "Dasol", "Infanta", "Labrador", "Laoac",
                        "Lingayen", "Mabini", "Malasiqui", "Manaoag", "Mangaldan", "Mangatarem",
                        "Mapandan", "Natividad", "Pozzorubio", "Rosales", "San Fabian", "San Jacinto",
                        "San Manuel", "San Nicolas", "San Quintin", "Santa Barbara", "Santa Maria",
                        "Santo Tomas", "Sison", "Sual", "Tayug", "Umingan", "Urbiztondo", "Villasis"
                }
        };

        // Region II (Cagayan Valley) Provinces
        public final String[] CAGAYAN_VALLEY_PROVINCES = {
                "Batanes", "Cagayan", "Isabela", "Nueva Vizcaya", "Quirino"
        };

        // Region II Cities/Municipalities by province
        public final String[][] CAGAYAN_VALLEY_MUNICIPALITIES = {
                // Batanes
                {
                        "Basco", "Itbayat", "Ivana", "Mahatao", "Sabtang", "Uyugan"
                },
                // Cagayan
                {
                        "Tuguegarao City", "Abulug", "Alcala", "Allacapan", "Amulung", "Aparri",
                        "Baggao", "Ballesteros", "Buguey", "Calayan", "Camalaniugan", "Claveria",
                        "Enrile", "Gattaran", "Gonzaga", "Iguig", "Lal-lo", "Lasam", "Pamplona",
                        "Peñablanca", "Piat", "Rizal", "Sanchez-Mira", "Santa Ana", "Santa Praxedes",
                        "Santa Teresita", "Santo Niño", "Solana", "Tuao"
                },
                // Isabela
                {
                        "Ilagan City", "Cauayan City", "Santiago City", "Alicia", "Angadanan",
                        "Aurora", "Benito Soliven", "Burgos", "Cabagan", "Cabatuan", "Cordon",
                        "Delfin Albano", "Dinapigue", "Divilacan", "Echague", "Gamu", "Jones",
                        "Luna", "Maconacon", "Mallig", "Naguilian", "Palanan", "Quezon", "Quirino",
                        "Ramon", "Reina Mercedes", "Roxas", "San Agustin", "San Guillermo",
                        "San Isidro", "San Manuel", "San Mariano", "San Mateo", "San Pablo",
                        "Santa Maria", "Santo Tomas", "Tumauini"
                },
                // Nueva Vizcaya
                {
                        "Bayombong", "Alfonso Castañeda", "Ambaguio", "Aritao", "Bagabag",
                        "Bambang", "Diadi", "Dupax del Norte", "Dupax del Sur", "Kasibu",
                        "Kayapa", "Quezon", "Santa Fe", "Solano", "Villaverde"
                },
                // Quirino
                {
                        "Cabarroguis", "Aglipay", "Diffun", "Maddela", "Nagtipunan", "Saguday"
                }
        };

        // Region III (Central Luzon) Provinces
        public final String[] CENTRAL_LUZON_PROVINCES = {
                "Aurora", "Bataan", "Bulacan", "Nueva Ecija", "Pampanga", "Tarlac", "Zambales"
        };

        // Region III Cities/Municipalities by province
        public final String[][] CENTRAL_LUZON_MUNICIPALITIES = {
                // Aurora
                {
                        "Baler", "Casiguran", "Dilasag", "Dinalungan", "Dingalan", "Dipaculao",
                        "Maria Aurora", "San Luis"
                },
                // Bataan
                {
                        "Balanga City", "Abucay", "Bagac", "Dinalupihan", "Hermosa", "Limay",
                        "Mariveles", "Morong", "Orani", "Orion", "Pilar", "Samal"
                },
                // Bulacan
                {
                        "Malolos City", "Meycauayan City", "San Jose del Monte City",
                        "Angat", "Balagtas", "Baliuag", "Bocaue", "Bulakan", "Bustos",
                        "Calumpit", "Doña Remedios Trinidad", "Guiguinto", "Hagonoy",
                        "Marilao", "Norzagaray", "Obando", "Pandi", "Paombong", "Plaridel",
                        "Pulilan", "San Ildefonso", "San Miguel", "San Rafael", "Santa Maria"
                },
                // Nueva Ecija
                {
                        "Cabanatuan City", "Gapan City", "Palayan City", "Science City of Muñoz",
                        "San Jose City", "Aliaga", "Bongabon", "Cabiao", "Carranglan", "Cuyapo",
                        "Gabaldon", "General Mamerto Natividad", "General Tinio", "Guimba",
                        "Jaen", "Laur", "Licab", "Llanera", "Lupao", "Nampicuan", "Pantabangan",
                        "Peñaranda", "Quezon", "Rizal", "San Antonio", "San Isidro", "San Leonardo",
                        "Santa Rosa", "Santo Domingo", "Talavera", "Talugtug", "Zaragoza"
                },
                // Pampanga
                {
                        "Angeles City", "San Fernando City", "Mabalacat City", "Apalit", "Arayat",
                        "Bacolor", "Candaba", "Floridablanca", "Guagua", "Lubao", "Macabebe",
                        "Magalang", "Masantol", "Mexico", "Minalin", "Porac", "San Luis",
                        "San Simon", "Santa Ana", "Santa Rita", "Santo Tomas", "Sasmuan"
                },
                // Tarlac
                {
                        "Tarlac City", "Anao", "Bamban", "Camiling", "Capas", "Concepcion",
                        "Gerona", "La Paz", "Mayantoc", "Moncada", "Paniqui", "Pura",
                        "Ramos", "San Clemente", "San Jose", "San Manuel", "Santa Ignacia",
                        "Victoria"
                },
                // Zambales
                {
                        "Olongapo City", "Botolan", "Cabangan", "Candelaria", "Castillejos",
                        "Iba", "Masinloc", "Palauig", "San Antonio", "San Felipe", "San Marcelino",
                        "San Narciso", "Santa Cruz", "Subic"
                }
        };

        // Region IV-A (CALABARZON) Provinces
        public final String[] CALABARZON_PROVINCES = {
                "Batangas", "Cavite", "Laguna", "Quezon", "Rizal"
        };

        // Region IV-A Cities/Municipalities by province
        public final String[][] CALABARZON_MUNICIPALITIES = {
                // Batangas
                {
                        "Batangas City", "Lipa City", "Tanauan City", "Agoncillo", "Alitagtag",
                        "Balayan", "Balete", "Bauan", "Calaca", "Calatagan", "Cuenca", "Ibaan",
                        "Laurel", "Lemery", "Lian", "Lobo", "Mabini", "Malvar", "Mataas na Kahoy",
                        "Nasugbu", "Padre Garcia", "Rosario", "San Jose", "San Juan", "San Luis",
                        "San Nicolas", "San Pascual", "Santa Teresita", "Santo Tomas", "Taal",
                        "Talisay", "Taysan", "Tingloy", "Tuy"
                },
                // Cavite
                {
                        "Bacoor City", "Cavite City", "Dasmariñas City", "General Trias City",
                        "Imus City", "Tagaytay City", "Trece Martires City", "Alfonso", "Amadeo",
                        "Carmona", "General Emilio Aguinaldo", "General Mariano Alvarez", "Indang",
                        "Kawit", "Magallanes", "Maragondon", "Mendez", "Naic", "Noveleta", "Rosario",
                        "Silang", "Tanza", "Ternate"
                },
                // Laguna
                {
                        "Biñan City", "Cabuyao City", "Calamba City", "San Pablo City",
                        "Santa Rosa City", "Alaminos", "Bay", "Calauan", "Cavinti", "Famy",
                        "Kalayaan", "Liliw", "Los Baños", "Luisiana", "Lumban", "Mabitac",
                        "Magdalena", "Majayjay", "Nagcarlan", "Paete", "Pagsanjan", "Pakil",
                        "Pangil", "Pila", "Rizal", "San Pedro City", "Santa Cruz", "Santa Maria",
                        "Siniloan", "Victoria"
                },
                // Quezon
                {
                        "Lucena City", "Tayabas City", "Agdangan", "Alabat", "Atimonan", "Buenavista",
                        "Burdeos", "Calauag", "Candelaria", "Catanauan", "Dolores", "General Luna",
                        "General Nakar", "Guinayangan", "Gumaca", "Infanta", "Jomalig", "Lopez",
                        "Lucban", "Macalelon", "Mauban", "Mulanay", "Padre Burgos", "Pagbilao",
                        "Panukulan", "Patnanungan", "Perez", "Pitogo", "Plaridel", "Polillo",
                        "Quezon", "Real", "Sampaloc", "San Andres", "San Antonio", "San Francisco",
                        "San Narciso", "Sariaya", "Tagkawayan", "Tiaong", "Unisan"
                },
                // Rizal
                {
                        "Antipolo City", "Angono", "Baras", "Binangonan", "Cainta", "Cardona",
                        "Jalajala", "Morong", "Pililla", "Rodriguez", "San Mateo", "Tanay",
                        "Taytay", "Teresa"
                }
        };

        // Region IV-B (MIMAROPA) Provinces
        public final String[] MIMAROPA_PROVINCES = {
                "Marinduque", "Occidental Mindoro", "Oriental Mindoro", "Palawan", "Romblon"
        };

        // Region IV-B Cities/Municipalities by province
        public final String[][] MIMAROPA_MUNICIPALITIES = {
                // Marinduque
                {
                        "Boac", "Buenavista", "Gasan", "Mogpog", "Santa Cruz", "Torrijos"
                },
                // Occidental Mindoro
                {
                        "Mamburao", "Abra de Ilog", "Calintaan", "Looc", "Lubang", "Magsaysay",
                        "Paluan", "Rizal", "Sablayan", "San Jose", "Santa Cruz", "Tilik"
                },
                // Oriental Mindoro
                {
                        "Calapan City", "Baco", "Bansud", "Bongabong", "Bulalacao", "Gloria",
                        "Mansalay", "Naujan", "Pinamalayan", "Pola", "Puerto Galera", "Roxas",
                        "San Teodoro", "Socorro", "Victoria"
                },
                // Palawan
                {
                        "Puerto Princesa City", "Aborlan", "Agutaya", "Araceli", "Balabac",
                        "Bataraza", "Brooke's Point", "Busuanga", "Cagayancillo", "Coron",
                        "Culion", "Cuyo", "Dumaran", "El Nido", "Kalayaan", "Linapacan",
                        "Magsaysay", "Narra", "Quezon", "Rizal", "Roxas", "San Vicente",
                        "Sofronio Española", "Taytay"
                },
                // Romblon
                {
                        "Romblon", "Alcantara", "Banton", "Cajidiocan", "Calatrava", "Concepcion",
                        "Corcuera", "Ferrol", "Looc", "Magdiwang", "Odiongan", "San Agustin",
                        "San Andres", "San Fernando", "San Jose", "Santa Fe", "Santa Maria"
                }
        };

        // Region V (Bicol) Provinces
        public final String[] BICOL_PROVINCES = {
                "Albay", "Camarines Norte", "Camarines Sur", "Catanduanes", "Masbate", "Sorsogon"
        };

        // Region V Cities/Municipalities by province
        public final String[][] BICOL_MUNICIPALITIES = {
                // Albay
                {
                        "Legazpi City", "Ligao City", "Tabaco City", "Bacacay", "Camalig",
                        "Daraga", "Guinobatan", "Jovellar", "Libon", "Malilipot", "Malinao",
                        "Manito", "Oas", "Pio Duran", "Polangui", "Rapu-Rapu", "Santo Domingo",
                        "Tiwi"
                },
                // Camarines Norte
                {
                        "Daet", "Basud", "Capalonga", "Jose Panganiban", "Labo", "Mercedes",
                        "Paracale", "San Lorenzo Ruiz", "San Vicente", "Santa Elena", "Talisay",
                        "Vinzons"
                },
                // Camarines Sur
                {
                        "Iriga City", "Naga City", "Baao", "Balatan", "Bato", "Bombon", "Buhi",
                        "Bula", "Cabusao", "Calabanga", "Camaligan", "Canaman", "Caramoan",
                        "Del Gallego", "Gainza", "Garchitorena", "Goa", "Lagonoy", "Libmanan",
                        "Lupi", "Magarao", "Milaor", "Minalabac", "Nabua", "Ocampo", "Pamplona",
                        "Pasacao", "Pili", "Presentacion", "Ragay", "Sagñay", "San Fernando",
                        "San Jose", "Sipocot", "Siruma", "Tigaon", "Tinambac"
                },
                // Catanduanes
                {
                        "Virac", "Bagamanoc", "Baras", "Bato", "Caramoran", "Gigmoto",
                        "Pandan", "Panganiban", "San Andres", "San Miguel", "Viga"
                },
                // Masbate
                {
                        "Masbate City", "Aroroy", "Baleno", "Balud", "Batuan", "Cataingan",
                        "Cawayan", "Claveria", "Dimasalang", "Esperanza", "Mandaon", "Milagros",
                        "Mobo", "Monreal", "Palanas", "Pio V. Corpuz", "Placer", "San Fernando",
                        "San Jacinto", "San Pascual", "Uson"
                },
                // Sorsogon
                {
                        "Sorsogon City", "Barcelona", "Bulan", "Bulusan", "Casiguran", "Castilla",
                        "Donsol", "Gubat", "Irosin", "Juban", "Magallanes", "Matnog", "Pilar",
                        "Prieto Diaz", "Santa Magdalena"
                }
        };

        // Region VI (Western Visayas) Provinces
        public final String[] WESTERN_VISAYAS_PROVINCES = {
                "Aklan", "Antique", "Capiz", "Guimaras", "Iloilo", "Negros Occidental"
        };

        // Region VI Cities/Municipalities by province
        public final String[][] WESTERN_VISAYAS_MUNICIPALITIES = {
                // Aklan
                {
                        "Kalibo", "Altavas", "Balete", "Banga", "Batan", "Buruanga", "Ibajay",
                        "Lezo", "Libacao", "Madalag", "Makato", "Malay", "Malinao", "Nabas",
                        "New Washington", "Numancia", "Tangalan"
                },
                // Antique
                {
                        "San Jose de Buenavista", "Anini-y", "Barbaza", "Belison", "Bugasong",
                        "Caluya", "Culasi", "Hamtic", "Laua-an", "Libertad", "Pandan", "Patnongon",
                        "Sebaste", "Sibalom", "Tibiao", "Tobias Fornier", "Valderrama"
                },
                // Capiz
                {
                        "Roxas City", "Cuartero", "Dao", "Dumalag", "Dumarao", "Ivisan",
                        "Jamindan", "Ma-ayon", "Mambusao", "Panay", "Panitan", "Pilar",
                        "Pontevedra", "President Roxas", "Sapian", "Sigma", "Tapaz"
                },
                // Guimaras
                {
                        "Jordan", "Buenavista", "Nueva Valencia", "San Lorenzo", "Sibunag"
                },
                // Iloilo
                {
                        "Iloilo City", "Passi City", "Ajuy", "Alimodian", "Anilao", "Badiangan",
                        "Balasan", "Banate", "Barotac Nuevo", "Barotac Viejo", "Batad", "Bingawan",
                        "Cabatuan", "Calinog", "Carles", "Concepcion", "Dingle", "Dueñas", "Dumangas",
                        "Estancia", "Guimbal", "Igbaras", "Janiuay", "Lambunao", "Leganes", "Lemery",
                        "Leon", "Maasin", "Miagao", "Mina", "New Lucena", "Oton", "Pavia", "Pototan",
                        "San Dionisio", "San Enrique", "San Joaquin", "San Miguel", "San Rafael",
                        "Santa Barbara", "Sara", "Tigbauan", "Tubungan", "Zarraga"
                },
                // Negros Occidental
                {
                        "Bacolod City", "Bago City", "Cadiz City", "Escalante City", "Himamaylan City",
                        "Kabankalan City", "La Carlota City", "Sagay City", "San Carlos City",
                        "Silay City", "Sipalay City", "Talisay City", "Victorias City", "Binalbagan",
                        "Calatrava", "Candoni", "Cauayan", "Enrique B. Magalona", "Hinigaran",
                        "Hinoba-an", "Ilog", "Isabela", "La Castellana", "Manapla", "Moises Padilla",
                        "Murcia", "Pontevedra", "Pulupandan", "Salvador Benedicto", "San Enrique",
                        "Toboso", "Valladolid"
                }
        };

        // Region VII (Central Visayas) Provinces
        public final String[] CENTRAL_VISAYAS_PROVINCES = {
                "Bohol", "Cebu", "Negros Oriental", "Siquijor"
        };

        // Region VII Cities/Municipalities by province
        public final String[][] CENTRAL_VISAYAS_MUNICIPALITIES = {
                // Bohol
                {
                        "Tagbilaran City", "Alburquerque", "Alicia", "Anda", "Antequera", "Baclayon",
                        "Balilihan", "Batuan", "Bien Unido", "Bilar", "Buenavista", "Calape", "Candijay",
                        "Carmen", "Catigbian", "Clarin", "Corella", "Cortes", "Dagohoy", "Danao",
                        "Dauis", "Dimiao", "Duero", "Garcia Hernandez", "Getafe", "Guindulman",
                        "Inabanga", "Jagna", "Lila", "Loay", "Loboc", "Loon", "Mabini", "Maribojoc",
                        "Panglao", "Pilar", "President Carlos P. Garcia", "Sagbayan", "San Isidro",
                        "San Miguel", "Sevilla", "Sierra Bullones", "Sikatuna", "Talibon", "Trinidad",
                        "Tubigon", "Ubay", "Valencia"
                },
                // Cebu
                {
                        "Cebu City", "Bogo City", "Carcar City", "Danao City", "Lapu-Lapu City",
                        "Mandaue City", "Naga City", "Talisay City", "Toledo City", "Alcantara",
                        "Alcoy", "Alegria", "Aloguinsan", "Argao", "Asturias", "Badian", "Balamban",
                        "Bantayan", "Barili", "Boljoon", "Borbon", "Carmen", "Catmon", "Compostela",
                        "Consolacion", "Cordova", "Daanbantayan", "Dalaguete", "Dumanjug", "Ginatilan",
                        "Liloan", "Madridejos", "Malabuyoc", "Medellin", "Minglanilla", "Moalboal",
                        "Oslob", "Pilar", "Pinamungajan", "Poro", "Ronda", "Samboan", "San Fernando",
                        "San Francisco", "San Remigio", "Santa Fe", "Santander", "Sibonga", "Sogod",
                        "Tabogon", "Tabuelan", "Tuburan", "Tudela"
                },
                // Negros Oriental
                {
                        "Dumaguete City", "Bais City", "Bayawan City", "Canlaon City", "Guihulngan City",
                        "Tanjay City", "Amlan", "Ayungon", "Bacong", "Basay", "Bindoy", "Dauin",
                        "Jimalalud", "La Libertad", "Mabinay", "Manjuyod", "Pamplona", "San Jose",
                        "Santa Catalina", "Siaton", "Sibulan", "Tayasan", "Valencia", "Vallehermoso",
                        "Zamboanguita"
                },
                // Siquijor
                {
                        "Siquijor", "Enrique Villanueva", "Larena", "Lazi", "Maria", "San Juan"
                }
        };

        // Region VIII (Eastern Visayas) Provinces
        public final String[] EASTERN_VISAYAS_PROVINCES = {
                "Biliran", "Eastern Samar", "Leyte", "Northern Samar", "Samar", "Southern Leyte"
        };

        // Region VIII Cities/Municipalities by province
        public final String[][] EASTERN_VISAYAS_MUNICIPALITIES = {
                // Biliran
                {
                        "Naval", "Almeria", "Biliran", "Cabucgayan", "Caibiran", "Culaba", "Kawayan", "Maripipi"
                },
                // Eastern Samar
                {
                        "Borongan City", "Arteche", "Balangiga", "Balangkayan", "Can-avid", "Dolores",
                        "General MacArthur", "Giporlos", "Guiuan", "Hernani", "Jipapad", "Lawaan",
                        "Llorente", "Maslog", "Maydolong", "Mercedes", "Oras", "Quinapondan",
                        "Salcedo", "San Julian", "San Policarpo", "Sulat", "Taft"
                },
                // Leyte
                {
                        "Tacloban City", "Baybay City", "Ormoc City", "Abuyog", "Alangalang", "Albuera",
                        "Babatngon", "Barugo", "Bato", "Burauen", "Calubian", "Capoocan", "Carigara",
                        "Dagami", "Dulag", "Hilongos", "Hindang", "Inopacan", "Isabel", "Jaro", "Javier",
                        "Julita", "Kananga", "La Paz", "Leyte", "MacArthur", "Mahaplag", "Matag-ob",
                        "Matalom", "Mayorga", "Merida", "Palo", "Palompon", "Pastrana", "San Isidro",
                        "San Miguel", "Santa Fe", "Tabango", "Tabontabon", "Tanauan", "Tolosa", "Tunga",
                        "Villaba"
                },
                // Northern Samar
                {
                        "Catarman", "Allen", "Biri", "Bobon", "Capul", "Catubig", "Gamay", "Laoang",
                        "Lapinig", "Las Navas", "Lavezares", "Lope de Vega", "Mapanas", "Mondragon",
                        "Palapag", "Pambujan", "Rosario", "San Antonio", "San Isidro", "San Jose",
                        "San Roque", "San Vicente", "Silvino Lobos", "Victoria"
                },
                // Samar
                {
                        "Catbalogan City", "Calbayog City", "Almagro", "Basey", "Calbiga", "Daram",
                        "Gandara", "Hinabangan", "Jiabong", "Marabut", "Matuguinao", "Motiong",
                        "Pagsanghan", "Paranas", "Pinabacdao", "San Jorge", "San Jose de Buan",
                        "San Sebastian", "Santa Margarita", "Santa Rita", "Santo Niño", "Tagapul-an",
                        "Talalora", "Tarangnan", "Villareal", "Zumarraga"
                },
                // Southern Leyte
                {
                        "Maasin City", "Anahawan", "Bontoc", "Hinunangan", "Hinundayan", "Libagon",
                        "Liloan", "Limasawa", "Macrohon", "Malitbog", "Padre Burgos", "Pintuyan",
                        "Saint Bernard", "San Francisco", "San Juan", "San Ricardo", "Silago",
                        "Sogod", "Tomas Oppus"
                }
        };

        // Region IX (Zamboanga Peninsula) Provinces
        public final String[] ZAMBOANGA_PENINSULA_PROVINCES = {
                "Zamboanga del Norte", "Zamboanga del Sur", "Zamboanga Sibugay"
        };

        // Region IX Cities/Municipalities by province
        public final String[][] ZAMBOANGA_PENINSULA_MUNICIPALITIES = {
                // Zamboanga del Norte
                {
                        "Dapitan City", "Dipolog City", "Baliguian", "Godod", "Gutalac", "Jose Dalman",
                        "Kalawit", "Katipunan", "La Libertad", "Labason", "Liloy", "Manukan", "Mutia",
                        "Piñan", "Polanco", "President Manuel A. Roxas", "Rizal", "Salug", "Sergio Osmeña Sr.",
                        "Siayan", "Sibuco", "Sibutad", "Sindangan", "Siocon", "Sirawai", "Tampilisan"
                },
                // Zamboanga del Sur
                {
                        "Zamboanga City", "Pagadian City", "Aurora", "Bayog", "Dimataling", "Dinas",
                        "Dumalinao", "Dumingag", "Guipos", "Josefina", "Kumalarang", "Labangan",
                        "Lakewood", "Lapuyan", "Mahayag", "Margosatubig", "Midsalip", "Molave",
                        "Pitogo", "Ramon Magsaysay", "San Miguel", "San Pablo", "Sominot",
                        "Tabina", "Tambulig", "Tigbao", "Tukuran", "Vincenzo A. Sagun"
                },
                // Zamboanga Sibugay
                {
                        "Ipil", "Alicia", "Buug", "Diplahan", "Imelda", "Kabasalan", "Mabuhay",
                        "Malangas", "Naga", "Olutanga", "Payao", "Roseller Lim", "Siay",
                        "Talusan", "Titay", "Tungawan"
                }
        };

        // Region X (Northern Mindanao) Provinces
        public final String[] NORTHERN_MINDANAO_PROVINCES = {
                "Bukidnon", "Camiguin", "Lanao del Norte", "Misamis Occidental", "Misamis Oriental"
        };

        // Region X Cities/Municipalities by province
        public final String[][] NORTHERN_MINDANAO_MUNICIPALITIES = {
                // Bukidnon
                {
                        "Malaybalay City", "Valencia City", "Baungon", "Cabanglasan", "Damulog",
                        "Dangcagan", "Don Carlos", "Impasugong", "Kadingilan", "Kalilangan",
                        "Kibawe", "Kitaotao", "Lantapan", "Libona", "Malitbog", "Manolo Fortich",
                        "Maramag", "Pangantucan", "Quezon", "San Fernando", "Sumilao", "Talakag"
                },
                // Camiguin
                {
                        "Mambajao", "Catarman", "Guinsiliban", "Mahinog", "Sagay"
                },
                // Lanao del Norte
                {
                        "Iligan City", "Bacolod", "Baloi", "Baroy", "Kapatagan", "Kauswagan", "Kolambugan",
                        "Lala", "Linamon", "Magsaysay", "Maigo", "Matungao", "Munai", "Nunungan",
                        "Pantao Ragat", "Pantar", "Poona Piagapo", "Salvador", "Sapad", "Sultan Naga Dimaporo",
                        "Tagoloan", "Tangcal", "Tubod"
                },
                // Misamis Occidental
                {
                        "Oroquieta City", "Ozamiz City", "Tangub City", "Aloran", "Baliangao",
                        "Bonifacio", "Calamba", "Clarin", "Concepcion", "Don Victoriano Chiongbian",
                        "Jimenez", "Lopez Jaena", "Panaon", "Plaridel", "Sapang Dalaga", "Sinacaban",
                        "Tudela"
                },
                // Misamis Oriental
                {
                        "Cagayan de Oro City", "El Salvador City", "Gingoog City", "Alubijid",
                        "Balingasag", "Balingoan", "Binuangan", "Claveria", "Gitagum", "Initao",
                        "Jasaan", "Kinoguitan", "Lagonglong", "Laguindingan", "Libertad", "Lugait",
                        "Magsaysay", "Manticao", "Medina", "Naawan", "Opol", "Salay", "Sugbongcogon",
                        "Tagoloan", "Talisayan", "Villanueva"
                }
        };

        // Region XI (Davao Region) Provinces
        public final String[] DAVAO_PROVINCES = {
                "Davao de Oro", "Davao del Norte", "Davao del Sur", "Davao Occidental", "Davao Oriental"
        };

        // Region XI Cities/Municipalities by province
        public final String[][] DAVAO_MUNICIPALITIES = {
                // Davao de Oro (formerly Compostela Valley)
                {
                        "Nabunturan", "Compostela", "Laak", "Mabini", "Maco", "Maragusan",
                        "Mawab", "Monkayo", "Montevelarde", "New Bataan", "Pantukan"
                },
                // Davao del Norte
                {
                        "Panabo City", "Samal City", "Tagum City", "Asuncion", "Braulio E. Dujali",
                        "Carmen", "Kapalong", "New Corella", "San Isidro", "Santo Tomas", "Talaingod"
                },
                // Davao del Sur
                {
                        "Davao City", "Digos City", "Bansalan", "Hagonoy", "Kiblawan", "Magsaysay",
                        "Malalag", "Matanao", "Padada", "Santa Cruz", "Sulop"
                },
                // Davao Occidental
                {
                        "Malita", "Don Marcelino", "Jose Abad Santos", "Santa Maria", "Sarangani"
                },
                // Davao Oriental
                {
                        "Mati City", "Baganga", "Banaybanay", "Boston", "Caraga", "Cateel",
                        "Governor Generoso", "Lupon", "Manay", "San Isidro", "Tarragona"
                }
        };

        // Region XII (SOCCSKSARGEN) Provinces
        public final String[] SOCCSKSARGEN_PROVINCES = {
                "Cotabato", "Sarangani", "South Cotabato", "Sultan Kudarat"
        };

        // Region XII Cities/Municipalities by province
        public final String[][] SOCCSKSARGEN_MUNICIPALITIES = {
                // Cotabato (North Cotabato)
                {
                        "Kidapawan City", "Alamada", "Aleosan", "Antipas", "Arakan", "Banisilan",
                        "Carmen", "Kabacan", "Libungan", "M'lang", "Magpet", "Makilala", "Matalam",
                        "Midsayap", "Pigkawayan", "Pikit", "President Roxas", "Tulunan"
                },
                // Sarangani
                {
                        "Alabel", "Glan", "Kiamba", "Maasim", "Maitum", "Malapatan", "Malungon"
                },
                // South Cotabato
                {
                        "General Santos City", "Koronadal City", "Banga", "Lake Sebu", "Norala",
                        "Polomolok", "Santo Niño", "Surallah", "T'boli", "Tampakan", "Tantangan", "Tupi"
                },
                // Sultan Kudarat
                {
                        "Tacurong City", "Bagumbayan", "Columbio", "Esperanza", "Isulan", "Kalamansig",
                        "Lambayong", "Lebak", "Lutayan", "Palimbang", "President Quirino", "Senator Ninoy Aquino"
                }
        };

        // Region XIII (Caraga) Provinces
        public final String[] CARAGA_PROVINCES = {
                "Agusan del Norte", "Agusan del Sur", "Dinagat Islands", "Surigao del Norte", "Surigao del Sur"
        };

        // Region XIII Cities/Municipalities by province
        public final String[][] CARAGA_MUNICIPALITIES = {
                // Agusan del Norte
                {
                        "Butuan City", "Cabadbaran City", "Buenavista", "Carmen", "Jabonga", "Kitcharao",
                        "Las Nieves", "Magallanes", "Nasipit", "Remedios T. Romualdez", "Santiago", "Tubay"
                },
                // Agusan del Sur
                {
                        "Bayugan City", "Bunawan", "Esperanza", "La Paz", "Loreto", "Prosperidad",
                        "Rosario", "San Francisco", "San Luis", "Santa Josefa", "Sibagat", "Talacogon",
                        "Trento", "Veruela"
                },
                // Dinagat Islands
                {
                        "San Jose", "Basilisa", "Cagdianao", "Dinagat", "Libjo", "Loreto", "Tubajon"
                },
                // Surigao del Norte
                {
                        "Surigao City", "Alegria", "Bacuag", "Burgos", "Claver", "Dapa", "Del Carmen",
                        "General Luna", "Gigaquit", "Mainit", "Malimono", "Pilar", "Placer", "San Benito",
                        "San Francisco", "San Isidro", "Santa Monica", "Sison", "Socorro", "Tagana-an", "Tubod"
                },
                // Surigao del Sur
                {
                        "Bislig City", "Tandag City", "Barobo", "Bayabas", "Cagwait", "Cantilan",
                        "Carmen", "Carrascal", "Cortes", "Hinatuan", "Lanuza", "Lianga", "Lingig",
                        "Madrid", "Marihatag", "San Agustin", "San Miguel", "Tagbina", "Tago"
                }
        };

        // BARMM (Bangsamoro Autonomous Region in Muslim Mindanao) Provinces
        public final String[] BARMM_PROVINCES = {
                "Basilan", "Lanao del Sur", "Maguindanao del Norte", "Maguindanao del Sur", "Sulu", "Tawi-Tawi"
        };

        // BARMM Cities/Municipalities by province
        public final String[][] BARMM_MUNICIPALITIES = {
                // Basilan
                {
                        "Isabela City", "Lamitan City", "Akbar", "Al-Barka", "Hadji Mohammad Ajul",
                        "Hadji Muhtamad", "Lantawan", "Maluso", "Sumisip", "Tabuan-Lasa", "Tipo-Tipo", "Tuburan", "Ungkaya Pukan"
                },
                // Lanao del Sur
                {
                        "Marawi City", "Bacolod-Kalawi", "Balabagan", "Balindong", "Bayang", "Binidayan",
                        "Buadiposo-Buntong", "Bubong", "Butig", "Calanogas", "Ditsaan-Ramain", "Ganassi",
                        "Kapai", "Kapatagan", "Lumba-Bayabao", "Lumbaca-Unayan", "Lumbatan", "Lumbayanague",
                        "Madalum", "Madamba", "Maguing", "Malabang", "Marantao", "Marogong", "Masiu",
                        "Mulondo", "Pagayawan", "Piagapo", "Poona Bayabao", "Pualas", "Saguiaran",
                        "Sultan Dumalondong", "Picong", "Tagoloan II", "Tamparan", "Taraka", "Tubaran",
                        "Tugaya", "Wao"
                },
                // Maguindanao del Norte
                {
                        "Datu Odin Sinsuat", "Barira", "Buldon", "Datu Blah T. Sinsuat", "Kabuntalan",
                        "Matanog", "Northern Kabuntalan", "Parang", "Sultan Kudarat", "Sultan Mastura", "Upi"
                },
                // Maguindanao del Sur
                {
                        "Buluan", "Ampatuan", "Datu Abdullah Sangki", "Datu Anggal Midtimbang", "Datu Hoffer Ampatuan",
                        "Datu Montawal", "Datu Paglas", "Datu Piang", "Datu Salibo", "Datu Saudi-Ampatuan",
                        "Datu Unsay", "Gen. S. K. Pendatun", "Guindulungan", "Mamasapano", "Mangudadatu",
                        "Pagalungan", "Paglat", "Pandag", "Rajah Buayan", "Shariff Aguak", "Shariff Saydona Mustapha",
                        "South Upi", "Sultan sa Barongis", "Talayan", "Talitay"
                },
                // Sulu
                {
                        "Jolo", "Hadji Panglima Tahil", "Indanan", "Kalingalan Caluang", "Lugus",
                        "Luuk", "Maimbung", "Old Panamao", "Omar", "Pandami", "Panglima Estino",
                        "Pangutaran", "Parang", "Pata", "Patikul", "Siasi", "Talipao", "Tapul"
                },
                // Tawi-Tawi
                {
                        "Bongao", "Languyan", "Mapun", "Panglima Sugala", "Sapa-Sapa", "Sibutu",
                        "Simunul", "Sitangkai", "South Ubian", "Tandubas", "Turtle Islands"
                }
        };
    }
}