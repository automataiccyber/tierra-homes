# Tierra Homes - Android Mobile App

A comprehensive real estate mobile application that helps users explore, customize, and analyze Philippine properties with AI-powered price predictions and location-based insights.

## 📱 App Overview

Tierra Homes is an Android application designed to provide users with a complete real estate experience, from browsing properties to customizing homes and analyzing market trends. The app features AI-powered price prediction, location classification, and comprehensive property management tools.

## ✨ Key Features

### 🏠 Property Management
- **House Types**: Browse various property types including apartments, bungalows, condominiums, cottages, duplexes, farmhouses, and more
- **Customization**: Customize properties with different materials, finishes, and architectural elements
- **Floor Planning**: Multi-floor property layouts with detailed specifications
- **Budget Management**: Set and track budgets with real-time cost calculations

### 🎯 Location & Market Analysis
- **Philippine Location Data**: Comprehensive database of Philippine locations with island, region, province, and municipality classifications
- **Location Classifier**: AI-powered location classification system
- **Market Insights**: Real-time market trends and property sales analytics
- **Price Prediction**: TensorFlow Lite-powered AI model for property price predictions

### 🔐 User Management
- **Authentication**: Secure login and signup system with Firebase Authentication
- **Password Recovery**: Forgot password functionality
- **User Profiles**: Personalized user experience with location tracking

### 📊 Analytics & Insights
- **Sales Analytics**: Track property sales data with Firebase Realtime Database
- **Charts & Graphs**: Visual data representation using MPAndroidChart
- **Market Trends**: Analyze property market trends and volatility
- **Asset Management**: Comprehensive asset tracking and valuation

### 🎨 User Interface
- **Dark/Light Theme**: Automatic theme switching based on system preferences
- **Responsive Design**: Adaptive layouts for different screen sizes
- **Smooth Animations**: Lottie animations and custom transitions
- **Material Design**: Modern UI following Material Design principles

## 🛠 Technical Stack

### Core Technologies
- **Language**: Java 11
- **Platform**: Android (API 24+)
- **Target SDK**: 35
- **Minimum SDK**: 24

### Key Dependencies
- **Firebase**: Authentication, Realtime Database, Analytics
- **TensorFlow Lite**: AI/ML price prediction models
- **MPAndroidChart**: Data visualization and charts
- **Retrofit**: HTTP client for API calls
- **Lottie**: Animation support
- **Material Design**: UI components

### Architecture
- **Activities**: Main navigation and user flow management
- **Fragments**: Modular UI components for different features
- **Firebase Manager**: Centralized Firebase operations
- **Database Helper**: Local SQLite database management
- **Network Utils**: Network connectivity monitoring

## 📁 Project Structure

```
app/src/main/
├── java/com/example/tierrahomes/
│   ├── Activities/
│   │   ├── LoadingScreenActivity.java
│   │   ├── LoginScreenActivity.java
│   │   ├── SignupScreenActivity.java
│   │   ├── ForgotPasswordScreenActivity.java
│   │   ├── BudgetAndLocationActivity.java
│   │   └── MainScreenActivity.java
│   ├── Fragments/
│   │   ├── HomeFragment.java
│   │   ├── HouseFragment.java
│   │   ├── BuyFragment.java
│   │   ├── BuildFragment.java
│   │   ├── CustomizeFragment.java
│   │   ├── BudgetFragment.java
│   │   ├── LocationFragment.java
│   │   ├── FloorsFragment.java
│   │   ├── TotalFragment.java
│   │   ├── InsightsFragment.java
│   │   ├── AssetFragment.java
│   │   └── AboutFragment.java
│   ├── Utils/
│   │   ├── FirebaseManager.java
│   │   ├── DatabaseHelper.java
│   │   ├── NetworkUtils.java
│   │   ├── MaterialPriceCalculator.java
│   │   ├── LocationClassifier.java
│   │   ├── PricePredictor.java
│   │   └── PhilippineLocationData.java
│   └── Models/
│       └── PricePredictionModel.java
├── res/
│   ├── layout/          # UI layouts
│   ├── drawable/        # Images and graphics
│   ├── values/          # Strings, colors, themes
│   ├── anim/           # Animation files
│   └── raw/            # TensorFlow models
└── assets/
    ├── locationclassification.txt
    └── ph_location_classification.txt
```

## 🚀 Getting Started

### Prerequisites
- Android Studio Arctic Fox or later
- Android SDK API 24+
- Java 11 or later
- Google Firebase account

### Installation

1. **Clone the repository**
   ```bash
   git clone [repository-url]
   cd TierraHomes-V3
   ```

2. **Set up Firebase**
   - Create a new Firebase project
   - Download `google-services.json` and place it in the `app/` directory
   - Enable Authentication and Realtime Database in Firebase Console

3. **Configure TensorFlow Model**
   - Place your TensorFlow Lite model file (`price_prediction.tflite`) in `app/src/main/assets/`

4. **Build and Run**
   ```bash
   ./gradlew build
   ./gradlew installDebug
   ```

### Configuration

#### Firebase Setup
1. Enable Email/Password authentication in Firebase Console
2. Set up Realtime Database rules for user data
3. Configure Analytics (optional)

#### TensorFlow Model
The app uses a custom TensorFlow Lite model for price prediction. Ensure your model:
- Accepts float array input
- Returns 3 outputs: predicted price, trend confidence, volatility
- Is optimized for mobile deployment

## 🔧 Features in Detail

### AI Price Prediction
- **Model**: TensorFlow Lite-based price prediction
- **Input**: Historical price data
- **Output**: Predicted prices, trend confidence, market volatility
- **Integration**: Seamless integration with property analysis

### Location Classification
- **Database**: Comprehensive Philippine location data
- **Classification**: Island → Region → Province → Municipality hierarchy
- **AI Integration**: Machine learning-based location classification

### Property Customization
- **Materials**: Various construction materials and finishes
- **Architecture**: Different house types and styles
- **Cost Calculation**: Real-time material and labor cost estimation
- **Budget Tracking**: Integrated budget management system

### Market Analytics
- **Sales Tracking**: Firebase-based sales data collection
- **Visualization**: MPAndroidChart for data representation
- **Trend Analysis**: Market trend identification and analysis

## 📱 Screenshots

<img width="450" height="1015" alt="image" src="https://github.com/user-attachments/assets/17c4bcf0-8da9-4e9e-93a8-a289ecb34d50" />
<img width="450" height="1015" alt="image" src="https://github.com/user-attachments/assets/4e86a499-3a91-424b-ac2e-84ba166dfadc" />
<img width="450" height="1015" alt="image" src="https://github.com/user-attachments/assets/0e58a34f-13b9-40ca-a867-44557dfa7c86" />
<img width="450" height="1015" alt="image" src="https://github.com/user-attachments/assets/c5ac98d1-0a9c-475a-b262-0e45daa69f84" />
<img width="450" height="1015" alt="image" src="https://github.com/user-attachments/assets/5d587794-10b0-434d-ada5-3d29b1a68d8d" />
<img width="450" height="1015" alt="image" src="https://github.com/user-attachments/assets/33c57410-d2f1-45c7-8e4c-3c0a51b2d63e" />

## 🔒 Permissions

The app requires the following permissions:
- `INTERNET`: For Firebase and API connectivity
- `ACCESS_NETWORK_STATE`: For network monitoring
- `READ_PHONE_STATE`: For device identification

## 🧪 Testing

### Unit Tests
```bash
./gradlew test
```

### Instrumented Tests
```bash
./gradlew connectedAndroidTest
```

## 📄 License

This project is proprietary software. All rights reserved.

## 📞 Contact

For support or inquiries:
- **Email**: lieldarrenfajutagana@gmail.com
- **Developer**: Liel Darren Fajutagana

## 🔄 Version History

- **v1.0**: Initial release with core features
  - Property browsing and customization
  - AI price prediction
  - Location classification
  - User authentication
  - Market analytics

## 🤝 Contributing

This is a proprietary application. For feature requests or bug reports, please contact the developer team.

---

**Tierra Homes** - Your trusted partner in Philippine real estate exploration and analysis. 
