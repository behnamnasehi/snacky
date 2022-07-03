# Snacky

[![](https://jitpack.io/v/behnamnasehi/snacky.svg)](https://jitpack.io/#behnamnasehi/snacky)

Snacky inform users of a process that an app has performed or will perform. They appear temporarily, towards the bottom of the screen

## Installation

### Step 1:

Add the JitPack repository to your build file 

Add it in your root build.gradle at the end of repositories:

```bash
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
### Step 2:

Add the dependency

```bash
	dependencies {
	        implementation 'com.github.behnamnasehi:snacky:lastest'
	}
```

## implementation
EncryptedSharedPreferences Class.java

```java
public class EncryptedDataHolder {

    private static final String KEY_API_KEY = "api_key";
    private static final String KEY_STORE_ALIAS = "MyEncryptedSharedPreferences";

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;


    @RequiresApi(api = Build.VERSION_CODES.M)
    private KeyGenParameterSpec createKeyGenParameterSpec() {
        return new KeyGenParameterSpec.Builder(
                KEY_STORE_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT
        ).setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256)
                .build();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private MasterKey getMasterKey(Context context, KeyGenParameterSpec keyGenParameterSpec) throws GeneralSecurityException, IOException {
        return new MasterKey.Builder(context, KEY_STORE_ALIAS)
                .setKeyGenParameterSpec(keyGenParameterSpec)
                .build();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public EncryptedDataHolder(Context context) {
        try {
            pref = androidx.security.crypto.EncryptedSharedPreferences.create(
                    context,
                    KEY_STORE_ALIAS,
                    getMasterKey(context, createKeyGenParameterSpec()),
                    androidx.security.crypto.EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    androidx.security.crypto.EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
            editor = pref.edit();
            editor.apply();
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }

    }

    public String getApiKey() {
        return pref.getString(KEY_API_KEY, "");
    }

    public void setApiKey(String apiKey) {
        editor.putString(KEY_API_KEY, apiKey);
        editor.apply();
        editor.commit();
    }

}
```
## Usage

```java
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            encryptedDataHolder = new EncryptedDataHolder(this);
        }
        encryptedDataHolder.setApiKey("this is test from Behnam Nasehi");

        TextView textView = findViewById(R.id.txt);
        textView.setText(encryptedDataHolder.getApiKey());
```

## Contact Me 
Telegram : [Click Here For Opening My Telegram Profile](https://t.me/behnamnasehii)

Virgul : [Click Here For Opening My Virgul Profile](https://virgool.io/@behnamnasehi)

Medium: [Click Here For Opening My Medium Profile](https://medium.com/@behnammnasehi)
