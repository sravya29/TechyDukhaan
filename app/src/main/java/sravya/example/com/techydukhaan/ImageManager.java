package sravya.example.com.techydukhaan;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;

import java.io.InputStream;
import java.io.OutputStream;
import java.security.SecureRandom;

public class ImageManager {
    public static final String storageConnectionString = "DefaultEndpointsProtocol=https;"
            + "AccountName=tdukhaan;"
            + "AccountKey=KlYtZls020NCaBvuvtoqMw0gMNpg4WX2XbhGgCbiAMr7SnRdAFNPCbuVs15MwrB+cCAf9+IYO9GmfJKv4Px4EA==";

    private static CloudBlobContainer getContainer() throws Exception {
        CloudStorageAccount storageAccount = CloudStorageAccount
                .parse(storageConnectionString);
        CloudBlobClient blobClient = storageAccount.createCloudBlobClient();
        CloudBlobContainer container = blobClient.getContainerReference("images");
        return container;
    }

    public static String UploadImage(InputStream image, int imageLength) throws Exception {
        CloudBlobContainer container = getContainer();
        container.createIfNotExists();
        String imageName = randomString(10);
        CloudBlockBlob imageBlob = container.getBlockBlobReference(imageName);
        imageBlob.upload(image, imageLength);
        return imageName;
    }

    public static void GetImage(String name, OutputStream imageStream, long imageLength) throws Exception {
        CloudBlobContainer container = getContainer()   ;
        CloudBlockBlob blob = container.getBlockBlobReference(name);
        if(blob.exists()){
            blob.downloadAttributes();
            imageLength = blob.getProperties().getLength();
            blob.download(imageStream);
        }
    }

    public static void DeleteImage(String name) throws Exception {
        CloudBlobContainer container = getContainer()   ;
        CloudBlockBlob blob = container.getBlockBlobReference(name);
        blob.deleteIfExists();
    }

    static final String validChars = "abcdefghijklmnopqrstuvwxyz";
    static SecureRandom rnd = new SecureRandom();

    static String randomString( int len ){
        StringBuilder sb = new StringBuilder( len );
        for( int i = 0; i < len; i++ )
            sb.append( validChars.charAt( rnd.nextInt(validChars.length()) ) );
        return sb.toString();
    }

}
