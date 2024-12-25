# --- General ProGuard rules for feat_nfc module ---

# Keep all public classes and methods in the feat_nfc package
-keep public class com.mwkg.nfc.** { *; }

# Preserve annotations
-keepattributes *Annotation*

# Preserve method signatures for reflection
-keepattributes Signature, MethodParameters, EnclosingMethod, InnerClasses

# Preserve Serializable classes
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    private void readObjectNoData();
}

# --- Rules for java.lang.invoke package and StringConcatFactory ---

# Suppress warnings for missing StringConcatFactory
-dontwarn java.lang.invoke.StringConcatFactory

# Keep all classes from java.lang.invoke package
-keep class java.lang.invoke.** { *; }

# Explicitly keep StringConcatFactory
#-keep class java.lang.invoke.StringConcatFactory { *; }

# --- Specific ProGuard rules for feat_nfc classes ---

# Keep HiNfcResult and its public members
-keep class com.mwkg.nfc.model.HiNfcResult { *; }

# Keep HiNfcTag and its public members
-keep class com.mwkg.nfc.model.HiNfcTag { *; }

# Keep HiNfcParser utility class and its public methods
-keep class com.mwkg.nfc.util.HiNfcParser { *; }

# Keep HiNfcScanner class and its public methods
-keep class com.mwkg.nfc.util.HiNfcScanner { *; }

# Keep HiNfcTagListActivity class and its public methods
-keep class com.mwkg.nfc.view.HiNfcTagListActivity { *; }

# Keep HiNfcTagListViewModel class and its public members
-keep class com.mwkg.nfc.viewmodel.HiNfcTagListViewModel { *; }

# --- Additional generic rules for safety ---

# Keep Parcelable implementations
-keepclassmembers class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator CREATOR;
}

# Keep Data Binding generated classes
-keep class androidx.databinding.** { *; }
-keepclassmembers class androidx.databinding.** { *; }

# Keep Jetpack Compose-related classes
-keep class androidx.compose.** { *; }
-keep class kotlin.Unit { *; }

# Keep coroutines-related classes
-keep class kotlinx.coroutines.** { *; }

# Keep Gson models (if applicable)
#-keep class com.google.gson.** { *; }
#-keepclassmembers class * {
#    @com.google.gson.annotations.SerializedName <fields>;
#}