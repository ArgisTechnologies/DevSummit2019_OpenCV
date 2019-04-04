package com.logicpd.papapill.computervision.detection;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.logicpd.papapill.activities.MainActivity;
import com.logicpd.papapill.computervision.ImageUtility;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.ByteArrayOutputStream;
import java.io.File;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;


/*
 * This runs all the test images through the detection process and returns image files than can
 * be human reviewed to determine accuracy consistency in computer vision detection process.
 *
 * Currently the completion of this test is to human verify each image to make sure a valid pill
 * was selected. There are a few asserts that confirm that a image was returned, and that one or
 * more 'successful' results were returned, whether they are actually good or not takes a human's eye.
 * Automation for this in the future could include identifying the center of each valid pill selection
 * inside of the deadzone, and checking that the 'successes' are on those within a margin of error.
 * This would take creating specific information for each test.
 *
 * These tests produce images with results selected for each test.  These are pulled of of the
 * AndroidThings by the .bat file <<PullTestImages.bat>> in the configFiles directory part of this
 * project.  Run this batch file and review the images locally.  You might need to tweak the
 * directory to write to locally as a valid directory in your local system.
 *
 * NOTE: You MUST run PushTestImages.bat before these tests will run.  This is found in the
 * configFiles directory.  This will copy 91 images and create directories needed for this test to the
 * device.
 *
 * Brady Hustad - Logic PD - 2018-12-17 - Initial Creation
 * Brady Hustad - Logic PD - 2019-01-16 - Completed Pull Request
 */
@RunWith(AndroidJUnit4.class)
public class DetectorImagesTest {

    //Class Variables
    private static boolean setUpRun = false;
    private Detector mDetector;

    //Rules
    @Rule
    public ActivityTestRule<MainActivity> activityActivityTestRule = new ActivityTestRule<MainActivity>(MainActivity.class);

    //****************************************************************************
    //Setup and Teardowns
    //****************************************************************************
    @Before
    public void init() {
        mDetector = new Detector();
    }

    /*
     * This test is for the Abilify pill.  The user should expect a picture of small white almost
     * rectangular pills with 1-3 red dots centered on the pills.
     */
    @Test
    public void AbilifyTest() {
        Bitmap pic = ImageUtility.retrieveImage("testImages/Abilify");
        assertThat(pic, instanceOf(Bitmap.class));

        DetectionData dd = mDetector.processImage(convertBitmap(pic), 1, true);

        ImageUtility.storeImage(dd.getSelectedPill(), "/testImages/results/Abilify");

        File dir = Environment.getExternalStorageDirectory();
        String fileToGet = dir.getPath() + "/Pictures/testImages/results/Abilify.jpg";
        File testFile = new File(fileToGet);

        assertTrue(testFile.exists());
        assertTrue(dd.getSelectedPills().size() > 0);
    }

    /*
     * This test is for the Acamprosate pill.  The user should expect a picture of round white
     * pills with 77 on them, same red dots in center of pills.
     */
    @Test
    public void AcamprosateTest() {
        Bitmap pic = ImageUtility.retrieveImage("testImages/Acamprosate");
        assertThat(pic, instanceOf(Bitmap.class));

        DetectionData dd = mDetector.processImage(convertBitmap(pic), 1, true);

        ImageUtility.storeImage(dd.getSelectedPill(), "/testImages/results/Acamprosate");

        File dir = Environment.getExternalStorageDirectory();
        String fileToGet = dir.getPath() + "/Pictures/testImages/results/Acamprosate.jpg";
        File testFile = new File(fileToGet);

        assertTrue(testFile.exists());
        assertTrue(dd.getSelectedPills().size() > 0);
    }

    /*
     * This is a test for Acetaminophen.  The user should expect round white pills with a line
     * through the center.  Same red dot in the middle.
     */
    @Test
    public void AcetaminophenTest() {
        Bitmap pic = ImageUtility.retrieveImage("testImages/Acetaminophen");
        assertThat(pic, instanceOf(Bitmap.class));

        DetectionData dd = mDetector.processImage(convertBitmap(pic), 1, true);

        ImageUtility.storeImage(dd.getSelectedPill(), "/testImages/results/Acetaminophen");

        File dir = Environment.getExternalStorageDirectory();
        String fileToGet = dir.getPath() + "/Pictures/testImages/results/Acetaminophen.jpg";
        File testFile = new File(fileToGet);

        assertTrue(testFile.exists());
        assertTrue(dd.getSelectedPills().size() > 0);
    }

    /*
     * This test is for acetaminophen 500mg capsules.  The user should expect to see banded
     * blue, white and red capsules.  Same red dots.
     */
    @Test
    public void Acetaminophen_500mgTest() {
        Bitmap pic = ImageUtility.retrieveImage("testImages/Acetaminophen_500mg");
        assertThat(pic, instanceOf(Bitmap.class));

        DetectionData dd = mDetector.processImage(convertBitmap(pic), 1, true);

        ImageUtility.storeImage(dd.getSelectedPill(), "/testImages/results/Acetaminophen_500mg");

        File dir = Environment.getExternalStorageDirectory();
        String fileToGet = dir.getPath() + "/Pictures/testImages/results/Acetaminophen_500mg.jpg";
        File testFile = new File(fileToGet);

        assertTrue(testFile.exists());
        assertTrue(dd.getSelectedPills().size() > 0);
    }

    /*
     * This test is for acetaminophen caffeine pills.  The user should expect red shiny capsules.
     * Same red dots.
     */
    @Test
    public void Acetaminophen_caffeineTest() {
        Bitmap pic = ImageUtility.retrieveImage("testImages/Acetaminophen_caffeine");
        assertThat(pic, instanceOf(Bitmap.class));

        DetectionData dd = mDetector.processImage(convertBitmap(pic), 1, true);

        ImageUtility.storeImage(dd.getSelectedPill(), "/testImages/results/Acetaminophen_caffeine");

        File dir = Environment.getExternalStorageDirectory();
        String fileToGet = dir.getPath() + "/Pictures/testImages/results/Acetaminophen_caffeine.jpg";
        File testFile = new File(fileToGet);

        assertTrue(testFile.exists());
        assertTrue(dd.getSelectedPills().size() > 0);
    }

    /*
     * This test is for Acyclovir.  The user should expect pink pentagon pills.
     * Same red dots.
     */
    @Test
    public void AcyclovirTest() {
        Bitmap pic = ImageUtility.retrieveImage("testImages/Acyclovir");
        assertThat(pic, instanceOf(Bitmap.class));

        DetectionData dd = mDetector.processImage(convertBitmap(pic), 1, true);

        ImageUtility.storeImage(dd.getSelectedPill(), "/testImages/results/Acyclovir");

        File dir = Environment.getExternalStorageDirectory();
        String fileToGet = dir.getPath() + "/Pictures/testImages/results/Acyclovir.jpg";
        File testFile = new File(fileToGet);

        assertTrue(testFile.exists());
        assertTrue(dd.getSelectedPills().size() > 0);
    }

    /*
     * This test is for advil capsuless.  The user should expect park pink shiny capsules.
     * Same red dots.
     */
    @Test
    public void AdvilTest() {
        Bitmap pic = ImageUtility.retrieveImage("testImages/Advil");
        assertThat(pic, instanceOf(Bitmap.class));

        DetectionData dd = mDetector.processImage(convertBitmap(pic), 1, true);

        ImageUtility.storeImage(dd.getSelectedPill(), "/testImages/results/Advil");

        File dir = Environment.getExternalStorageDirectory();
        String fileToGet = dir.getPath() + "/Pictures/testImages/results/Advil.jpg";
        File testFile = new File(fileToGet);

        assertTrue(testFile.exists());
        assertTrue(dd.getSelectedPills().size() > 0);
    }

    /*
     * This test is for advil 2 tone capsules.  The user should expect half dark pink and light tan capsules.
     * Same red dots.
     */
    @Test
    public void AdvilTwoColorTest() {
        Bitmap pic = ImageUtility.retrieveImage("testImages/Advil_2_color");
        assertThat(pic, instanceOf(Bitmap.class));

        DetectionData dd = mDetector.processImage(convertBitmap(pic), 1, true);

        ImageUtility.storeImage(dd.getSelectedPill(), "/testImages/results/Advil_2_color");

        File dir = Environment.getExternalStorageDirectory();
        String fileToGet = dir.getPath() + "/Pictures/testImages/results/Advil_2_color.jpg";
        File testFile = new File(fileToGet);

        assertTrue(testFile.exists());
        assertTrue(dd.getSelectedPills().size() > 0);
    }

    /*
     * This test is for Alendronate pills.  The user should expect small white pills.
     * Same red dots.
     */
    @Test
    public void AlendronateTest() {
        Bitmap pic = ImageUtility.retrieveImage("testImages/Alendronate");
        assertThat(pic, instanceOf(Bitmap.class));

        DetectionData dd = mDetector.processImage(convertBitmap(pic), 1, true);

        ImageUtility.storeImage(dd.getSelectedPill(), "/testImages/results/Alendronate");

        File dir = Environment.getExternalStorageDirectory();
        String fileToGet = dir.getPath() + "/Pictures/testImages/results/Alendronate.jpg";
        File testFile = new File(fileToGet);

        assertTrue(testFile.exists());
        assertTrue(dd.getSelectedPills().size() > 0);
    }

    /*
     * This test is for Alprazolam_2mg_quarter pills.  The user should expect tiny white pills with an x.
     * Same red dots.
     */
    @Test
    public void AlprazolamTwoQuarterTest() {
        Bitmap pic = ImageUtility.retrieveImage("testImages/Alprazolam_2mg_quarter");
        assertThat(pic, instanceOf(Bitmap.class));

        DetectionData dd = mDetector.processImage(convertBitmap(pic), 1, true);

        ImageUtility.storeImage(dd.getSelectedPill(), "/testImages/results/Alprazolam_2mg_quarter");

        File dir = Environment.getExternalStorageDirectory();
        String fileToGet = dir.getPath() + "/Pictures/testImages/results/Alprazolam_2mg_quarter.jpg";
        File testFile = new File(fileToGet);

        assertTrue(testFile.exists());
        assertTrue(dd.getSelectedPills().size() > 0);
    }

    /*
     * This test is for Amiloride pills.  The user should expect small white diamond pills.
     * Same red dots.
     */
    @Test
    public void AmilorideTest() {
        Bitmap pic = ImageUtility.retrieveImage("testImages/Amiloride");
        assertThat(pic, instanceOf(Bitmap.class));

        DetectionData dd = mDetector.processImage(convertBitmap(pic), 1, true);

        ImageUtility.storeImage(dd.getSelectedPill(), "/testImages/results/Amiloride");

        File dir = Environment.getExternalStorageDirectory();
        String fileToGet = dir.getPath() + "/Pictures/testImages/results/Amiloride.jpg";
        File testFile = new File(fileToGet);

        assertTrue(testFile.exists());
        assertTrue(dd.getSelectedPills().size() > 0);
    }

    /*
     * This test is for Amlodipine pills.  The user should expect small white triangle pills.
     * Same red dots.
     */
    @Test
    public void AmlodipineTest() {
        Bitmap pic = ImageUtility.retrieveImage("testImages/Amlodipine");
        assertThat(pic, instanceOf(Bitmap.class));

        DetectionData dd = mDetector.processImage(convertBitmap(pic), 1, true);

        ImageUtility.storeImage(dd.getSelectedPill(), "/testImages/results/Amlodipine");

        File dir = Environment.getExternalStorageDirectory();
        String fileToGet = dir.getPath() + "/Pictures/testImages/results/Amlodipine.jpg";
        File testFile = new File(fileToGet);

        assertTrue(testFile.exists());
        assertTrue(dd.getSelectedPills().size() > 0);
    }

    /*
     * This test is for Amlodipine_5mg pills.  The user should expect small white capsule shaped pills.
     * Same red dots.
     */
    @Test
    public void AmlodipineFiveTest() {
        Bitmap pic = ImageUtility.retrieveImage("testImages/Amlodipine_5mg");
        assertThat(pic, instanceOf(Bitmap.class));

        DetectionData dd = mDetector.processImage(convertBitmap(pic), 1, true);

        ImageUtility.storeImage(dd.getSelectedPill(), "/testImages/results/Amlodipine_5mg");

        File dir = Environment.getExternalStorageDirectory();
        String fileToGet = dir.getPath() + "/Pictures/testImages/results/Amlodipine_5mg.jpg";
        File testFile = new File(fileToGet);

        assertTrue(testFile.exists());
        assertTrue(dd.getSelectedPills().size() > 0);
    }

    /*
     * This test is for Amlodipine_Valsartan pills.  The user should expect small yellow/orange ellipse pills.
     * Same red dots.
     */
    @Test
    public void AmlodipineValsartanTest() {
        Bitmap pic = ImageUtility.retrieveImage("testImages/Amlodipine_Valsartan");
        assertThat(pic, instanceOf(Bitmap.class));

        DetectionData dd = mDetector.processImage(convertBitmap(pic), 1, true);

        ImageUtility.storeImage(dd.getSelectedPill(), "/testImages/results/Amlodipine_Valsartan");

        File dir = Environment.getExternalStorageDirectory();
        String fileToGet = dir.getPath() + "/Pictures/testImages/results/Amlodipine_Valsartan.jpg";
        File testFile = new File(fileToGet);

        assertTrue(testFile.exists());
        assertTrue(dd.getSelectedPills().size() > 0);
    }

    /*
     * This test is for Azasan pills.  The user should expect larger light yellow triangle pills.
     * Same red dots.
     */
    @Test
    public void AzasanTest() {
        Bitmap pic = ImageUtility.retrieveImage("testImages/Azasan");
        assertThat(pic, instanceOf(Bitmap.class));

        DetectionData dd = mDetector.processImage(convertBitmap(pic), 1, true);

        ImageUtility.storeImage(dd.getSelectedPill(), "/testImages/results/Azasan");

        File dir = Environment.getExternalStorageDirectory();
        String fileToGet = dir.getPath() + "/Pictures/testImages/results/Azasan.jpg";
        File testFile = new File(fileToGet);

        assertTrue(testFile.exists());
        assertTrue(dd.getSelectedPills().size() > 0);
    }

    /*
     * This test is for Buspirone pills.  The user should expect white long rectangle pills.
     * Same red dots.
     */
    @Test
    public void BuspironeTest() {
        Bitmap pic = ImageUtility.retrieveImage("testImages/Buspirone");
        assertThat(pic, instanceOf(Bitmap.class));

        DetectionData dd = mDetector.processImage(convertBitmap(pic), 1, true);

        ImageUtility.storeImage(dd.getSelectedPill(), "/testImages/results/Buspirone");

        File dir = Environment.getExternalStorageDirectory();
        String fileToGet = dir.getPath() + "/Pictures/testImages/results/Buspirone.jpg";
        File testFile = new File(fileToGet);

        assertTrue(testFile.exists());
        assertTrue(dd.getSelectedPills().size() > 0);
    }

    /*
     * This test is for Buspirone_5mg pills.  The user should expect small white rounded rectangle pills.
     * Same red dots.
     */
    @Test
    public void BuspironeFiveTest() {
        Bitmap pic = ImageUtility.retrieveImage("testImages/Buspirone_5mg");
        assertThat(pic, instanceOf(Bitmap.class));

        DetectionData dd = mDetector.processImage(convertBitmap(pic), 1, true);

        ImageUtility.storeImage(dd.getSelectedPill(), "/testImages/results/Buspirone_5mg");

        File dir = Environment.getExternalStorageDirectory();
        String fileToGet = dir.getPath() + "/Pictures/testImages/results/Buspirone_5mg.jpg";
        File testFile = new File(fileToGet);

        assertTrue(testFile.exists());
        assertTrue(dd.getSelectedPills().size() > 0);
    }

    /*
     * This test is for Campral pills.  The user should expect small round pills.
     * Same red dots.
     */
    @Test
    public void CampralTest() {
        Bitmap pic = ImageUtility.retrieveImage("testImages/Campral");
        assertThat(pic, instanceOf(Bitmap.class));

        DetectionData dd = mDetector.processImage(convertBitmap(pic), 1, true);

        ImageUtility.storeImage(dd.getSelectedPill(), "/testImages/results/Campral");

        File dir = Environment.getExternalStorageDirectory();
        String fileToGet = dir.getPath() + "/Pictures/testImages/results/Campral.jpg";
        File testFile = new File(fileToGet);

        assertTrue(testFile.exists());
        assertTrue(dd.getSelectedPills().size() > 0);
    }

    /*
     * This test is for Cimetidine pills.  The user should expect larger green pentagon pills.
     * Same red dots.
     */
    @Test
    public void CimetidineTest() {
        Bitmap pic = ImageUtility.retrieveImage("testImages/Cimetidine");
        assertThat(pic, instanceOf(Bitmap.class));

        DetectionData dd = mDetector.processImage(convertBitmap(pic), 1, true);

        ImageUtility.storeImage(dd.getSelectedPill(), "/testImages/results/Cimetidine");

        File dir = Environment.getExternalStorageDirectory();
        String fileToGet = dir.getPath() + "/Pictures/testImages/results/Cimetidine.jpg";
        File testFile = new File(fileToGet);

        assertTrue(testFile.exists());
        assertTrue(dd.getSelectedPills().size() > 0);
    }

    /*
     * This test is for Cordarone pills.  The user should expect larger pink round pills.
     * Same red dots.
     */
    @Test
    public void CordaroneTest() {
        Bitmap pic = ImageUtility.retrieveImage("testImages/Cordarone");
        assertThat(pic, instanceOf(Bitmap.class));

        DetectionData dd = mDetector.processImage(convertBitmap(pic), 1, true);

        ImageUtility.storeImage(dd.getSelectedPill(), "/testImages/results/Cordarone");

        File dir = Environment.getExternalStorageDirectory();
        String fileToGet = dir.getPath() + "/Pictures/testImages/results/Cordarone.jpg";
        File testFile = new File(fileToGet);

        assertTrue(testFile.exists());
        assertTrue(dd.getSelectedPills().size() > 0);
    }

    /*
     * This test is for Coreg pills.  The user should expect light yellow Ellipse pills.
     * Same red dots.
     */
    @Test
    public void CoregTest() {
        Bitmap pic = ImageUtility.retrieveImage("testImages/Coreg");
        assertThat(pic, instanceOf(Bitmap.class));

        DetectionData dd = mDetector.processImage(convertBitmap(pic), 1, true);

        ImageUtility.storeImage(dd.getSelectedPill(), "/testImages/results/Coreg");

        File dir = Environment.getExternalStorageDirectory();
        String fileToGet = dir.getPath() + "/Pictures/testImages/results/Coreg.jpg";
        File testFile = new File(fileToGet);

        assertTrue(testFile.exists());
        assertTrue(dd.getSelectedPills().size() > 0);
    }

    /*
     * This test is for Daklinza pills.  The user should expect green hexagonal pills.
     * Same red dots.
     */
    @Test
    public void DaklinzaTest() {
        Bitmap pic = ImageUtility.retrieveImage("testImages/Daklinza");
        assertThat(pic, instanceOf(Bitmap.class));

        DetectionData dd = mDetector.processImage(convertBitmap(pic), 1, true);

        ImageUtility.storeImage(dd.getSelectedPill(), "/testImages/results/Daklinza");

        File dir = Environment.getExternalStorageDirectory();
        String fileToGet = dir.getPath() + "/Pictures/testImages/results/Daklinza.jpg";
        File testFile = new File(fileToGet);

        assertTrue(testFile.exists());
        assertTrue(dd.getSelectedPills().size() > 0);
    }

    /*
     * This test is for DDAVP pills.  The user should expect white oval pills.
     * Same red dots.
     */
    @Test
    public void DDAVPTest() {
        Bitmap pic = ImageUtility.retrieveImage("testImages/DDAVP");
        assertThat(pic, instanceOf(Bitmap.class));

        DetectionData dd = mDetector.processImage(convertBitmap(pic), 1, true);

        ImageUtility.storeImage(dd.getSelectedPill(), "/testImages/results/DDAVP");

        File dir = Environment.getExternalStorageDirectory();
        String fileToGet = dir.getPath() + "/Pictures/testImages/results/DDAVP.jpg";
        File testFile = new File(fileToGet);

        assertTrue(testFile.exists());
        assertTrue(dd.getSelectedPills().size() > 0);
    }

    /*
     * This test is for Dexmethylphenidate pills.  The user should expect large blue odd shaped pills.
     * Same red dots.
     */
    @Test
    public void DexmethylphenidateTest() {
        Bitmap pic = ImageUtility.retrieveImage("testImages/Dexmethylphenidate");
        assertThat(pic, instanceOf(Bitmap.class));

        DetectionData dd = mDetector.processImage(convertBitmap(pic), 1, true);

        ImageUtility.storeImage(dd.getSelectedPill(), "/testImages/results/Dexmethylphenidate");

        File dir = Environment.getExternalStorageDirectory();
        String fileToGet = dir.getPath() + "/Pictures/testImages/results/Dexmethylphenidate.jpg";
        File testFile = new File(fileToGet);

        assertTrue(testFile.exists());
        assertTrue(dd.getSelectedPills().size() > 0);
    }

    /*
     * This test is for Diflucan pills.  The user should expect light pick trapezoid pills.
     * Same red dots.
     */
    @Test
    public void DiflucanTest() {
        Bitmap pic = ImageUtility.retrieveImage("testImages/Diflucan");
        assertThat(pic, instanceOf(Bitmap.class));

        DetectionData dd = mDetector.processImage(convertBitmap(pic), 1, true);

        ImageUtility.storeImage(dd.getSelectedPill(), "/testImages/results/Diflucan");

        File dir = Environment.getExternalStorageDirectory();
        String fileToGet = dir.getPath() + "/Pictures/testImages/results/Diflucan.jpg";
        File testFile = new File(fileToGet);

        assertTrue(testFile.exists());
        assertTrue(dd.getSelectedPills().size() > 0);
    }

    /*
     * This test is for Doryx pills.  The user should expect white 3 section rectangle pills.
     * Same red dots.
     */
    @Test
    public void DoryxTest() {
        Bitmap pic = ImageUtility.retrieveImage("testImages/Doryx");
        assertThat(pic, instanceOf(Bitmap.class));

        DetectionData dd = mDetector.processImage(convertBitmap(pic), 1, true);

        ImageUtility.storeImage(dd.getSelectedPill(), "/testImages/results/Doryx");

        File dir = Environment.getExternalStorageDirectory();
        String fileToGet = dir.getPath() + "/Pictures/testImages/results/Doryx.jpg";
        File testFile = new File(fileToGet);

        assertTrue(testFile.exists());
        assertTrue(dd.getSelectedPills().size() > 0);
    }

    /*
     * This test is for Estazolam pills.  The user should expect white small diamond pills.
     * Same red dots.
     */
    @Test
    public void EstazolamTest() {
        Bitmap pic = ImageUtility.retrieveImage("testImages/Estazolam");
        assertThat(pic, instanceOf(Bitmap.class));

        DetectionData dd = mDetector.processImage(convertBitmap(pic), 1, true);

        ImageUtility.storeImage(dd.getSelectedPill(), "/testImages/results/Estazolam");

        File dir = Environment.getExternalStorageDirectory();
        String fileToGet = dir.getPath() + "/Pictures/testImages/results/Estazolam.jpg";
        File testFile = new File(fileToGet);

        assertTrue(testFile.exists());
        assertTrue(dd.getSelectedPills().size() > 0);
    }

    /*
     * This test is for Etidronate_Disodium pills.  The user should expect white  rectangle pills.
     * Same red dots.
     */
    @Test
    public void EtidronateDisodiumTest() {
        Bitmap pic = ImageUtility.retrieveImage("testImages/Etidronate_Disodium");
        assertThat(pic, instanceOf(Bitmap.class));

        DetectionData dd = mDetector.processImage(convertBitmap(pic), 1, true);

        ImageUtility.storeImage(dd.getSelectedPill(), "/testImages/results/Etidronate_Disodium");

        File dir = Environment.getExternalStorageDirectory();
        String fileToGet = dir.getPath() + "/Pictures/testImages/results/Etidronate_Disodium.jpg";
        File testFile = new File(fileToGet);

        assertTrue(testFile.exists());
        assertTrue(dd.getSelectedPills().size() > 0);
    }

    /*
     * This test is for Etodolac pills.  The user should expect white surfboardish pills.
     * Same red dots.
     */
    @Test
    public void EtodolacTest() {
        Bitmap pic = ImageUtility.retrieveImage("testImages/Etodolac");
        assertThat(pic, instanceOf(Bitmap.class));

        DetectionData dd = mDetector.processImage(convertBitmap(pic), 1, true);

        ImageUtility.storeImage(dd.getSelectedPill(), "/testImages/results/Etodolac");

        File dir = Environment.getExternalStorageDirectory();
        String fileToGet = dir.getPath() + "/Pictures/testImages/results/Etodolac.jpg";
        File testFile = new File(fileToGet);

        assertTrue(testFile.exists());
        assertTrue(dd.getSelectedPills().size() > 0);
    }

    /*
     * This test is for Exemestane pills.  The user should expect white 2858 ball pills.
     * Same red dots.
     */
    @Test
    public void ExemestaneTest() {
        Bitmap pic = ImageUtility.retrieveImage("testImages/Exemestane");
        assertThat(pic, instanceOf(Bitmap.class));

        DetectionData dd = mDetector.processImage(convertBitmap(pic), 1, true);

        ImageUtility.storeImage(dd.getSelectedPill(), "/testImages/results/Exemestane");

        File dir = Environment.getExternalStorageDirectory();
        String fileToGet = dir.getPath() + "/Pictures/testImages/results/Exemestane.jpg";
        File testFile = new File(fileToGet);

        assertTrue(testFile.exists());
        assertTrue(dd.getSelectedPills().size() > 0);
    }

    /*
     * This test is for Famotidine pills.  The user should expect tan squarish rectangle pills.
     * Same red dots.
     */
    @Test
    public void FamotidineTest() {
        Bitmap pic = ImageUtility.retrieveImage("testImages/Famotidine");
        assertThat(pic, instanceOf(Bitmap.class));

        DetectionData dd = mDetector.processImage(convertBitmap(pic), 1, true);

        ImageUtility.storeImage(dd.getSelectedPill(), "/testImages/results/Famotidine");

        File dir = Environment.getExternalStorageDirectory();
        String fileToGet = dir.getPath() + "/Pictures/testImages/results/Famotidine.jpg";
        File testFile = new File(fileToGet);

        assertTrue(testFile.exists());
        assertTrue(dd.getSelectedPills().size() > 0);
    }

    /*
     * This test is for Fanapt pills.  The user should expect white circle with printed hexagon pills.
     * Same red dots.
     */
    @Test
    public void FanaptTest() {
        Bitmap pic = ImageUtility.retrieveImage("testImages/Fanapt");
        assertThat(pic, instanceOf(Bitmap.class));

        DetectionData dd = mDetector.processImage(convertBitmap(pic), 1, true);

        ImageUtility.storeImage(dd.getSelectedPill(), "/testImages/results/Fanapt");

        File dir = Environment.getExternalStorageDirectory();
        String fileToGet = dir.getPath() + "/Pictures/testImages/results/Fanapt.jpg";
        File testFile = new File(fileToGet);

        assertTrue(testFile.exists());
        assertTrue(dd.getSelectedPills().size() > 0);
    }

    /*
     * This test is for FerraPlus pills.  The user should expect dark camo ellipse pills.
     * Same red dots.
     */
    @Test
    public void FerraPlusTest() {
        Bitmap pic = ImageUtility.retrieveImage("testImages/FerraPlus");
        assertThat(pic, instanceOf(Bitmap.class));

        DetectionData dd = mDetector.processImage(convertBitmap(pic), 1, true);

        ImageUtility.storeImage(dd.getSelectedPill(), "/testImages/results/FerraPlus");

        File dir = Environment.getExternalStorageDirectory();
        String fileToGet = dir.getPath() + "/Pictures/testImages/results/FerraPlus.jpg";
        File testFile = new File(fileToGet);

        assertTrue(testFile.exists());
        assertTrue(dd.getSelectedPills().size() > 0);
    }

    /*
     * This test is for Fluconazole pills.  The user should expect pink eye shaped pills.
     * Same red dots.
     */
    @Test
    public void FluconazoleTest() {
        Bitmap pic = ImageUtility.retrieveImage("testImages/Fluconazole");
        assertThat(pic, instanceOf(Bitmap.class));

        DetectionData dd = mDetector.processImage(convertBitmap(pic), 1, true);

        ImageUtility.storeImage(dd.getSelectedPill(), "/testImages/results/Fluconazole");

        File dir = Environment.getExternalStorageDirectory();
        String fileToGet = dir.getPath() + "/Pictures/testImages/results/Fluconazole.jpg";
        File testFile = new File(fileToGet);

        assertTrue(testFile.exists());
        assertTrue(dd.getSelectedPills().size() > 0);
    }

    /*
     * This test is for Fluconazole_100mg pills.  The user should expect pink trapezoid pills.
     * Same red dots.
     */
    @Test
    public void FluconazoleHundredMgTest() {
        Bitmap pic = ImageUtility.retrieveImage("testImages/Fluconazole_100mg");
        assertThat(pic, instanceOf(Bitmap.class));

        DetectionData dd = mDetector.processImage(convertBitmap(pic), 1, true);

        ImageUtility.storeImage(dd.getSelectedPill(), "/testImages/results/Fluconazole_100mg");

        File dir = Environment.getExternalStorageDirectory();
        String fileToGet = dir.getPath() + "/Pictures/testImages/results/Fluconazole_100mg.jpg";
        File testFile = new File(fileToGet);

        assertTrue(testFile.exists());
        assertTrue(dd.getSelectedPills().size() > 0);
    }

    /*
     * This test is for Fosinopril pills.  The user should expect white figure 8 pills.
     * Same red dots.
     */
    @Test
    public void FosinoprilTest() {
        Bitmap pic = ImageUtility.retrieveImage("testImages/Fosinopril");
        assertThat(pic, instanceOf(Bitmap.class));

        DetectionData dd = mDetector.processImage(convertBitmap(pic), 1, true);

        ImageUtility.storeImage(dd.getSelectedPill(), "/testImages/results/Fosinopril");

        File dir = Environment.getExternalStorageDirectory();
        String fileToGet = dir.getPath() + "/Pictures/testImages/results/Fosinopril.jpg";
        File testFile = new File(fileToGet);

        assertTrue(testFile.exists());
        assertTrue(dd.getSelectedPills().size() > 0);
    }

    /*
     * This test is for Glipizide_hole pills.  The user should expect pink with smile pills.
     * Same red dots.
     */
    @Test
    public void GlipizideHoleTest() {
        Bitmap pic = ImageUtility.retrieveImage("testImages/Glipizide_hole");
        assertThat(pic, instanceOf(Bitmap.class));

        DetectionData dd = mDetector.processImage(convertBitmap(pic), 1, true);

        ImageUtility.storeImage(dd.getSelectedPill(), "/testImages/results/Glipizide_hole");

        File dir = Environment.getExternalStorageDirectory();
        String fileToGet = dir.getPath() + "/Pictures/testImages/results/Glipizide_hole.jpg";
        File testFile = new File(fileToGet);

        assertTrue(testFile.exists());
        assertTrue(dd.getSelectedPills().size() > 0);
    }

    /*
     * This test is for Glyburide_Micronized pills.  The user should expect dark red capsule pills.
     * Same red dots.
     */
    @Test
    public void GlyburideMicronizedTest() {
        Bitmap pic = ImageUtility.retrieveImage("testImages/Glyburide_Micronized");
        assertThat(pic, instanceOf(Bitmap.class));

        DetectionData dd = mDetector.processImage(convertBitmap(pic), 1, true);

        ImageUtility.storeImage(dd.getSelectedPill(), "/testImages/results/Glyburide_Micronized");

        File dir = Environment.getExternalStorageDirectory();
        String fileToGet = dir.getPath() + "/Pictures/testImages/results/Glyburide_Micronized.jpg";
        File testFile = new File(fileToGet);

        assertTrue(testFile.exists());
        assertTrue(dd.getSelectedPills().size() > 0);
    }

    /*
     * This test is for Glyxambi pills.  The user should expect pink rounded triangle pills.
     * Same red dots.
     */
    @Test
    public void GlyxambiTest() {
        Bitmap pic = ImageUtility.retrieveImage("testImages/Glyxambi");
        assertThat(pic, instanceOf(Bitmap.class));

        DetectionData dd = mDetector.processImage(convertBitmap(pic), 1, true);

        ImageUtility.storeImage(dd.getSelectedPill(), "/testImages/results/Glyxambi");

        File dir = Environment.getExternalStorageDirectory();
        String fileToGet = dir.getPath() + "/Pictures/testImages/results/Glyxambi.jpg";
        File testFile = new File(fileToGet);

        assertTrue(testFile.exists());
        assertTrue(dd.getSelectedPills().size() > 0);
    }

    /*
     * This test is for Gralise pills.  The user should expect white large oval pills.
     * Same red dots.
     */
    @Test
    public void GraliseTest() {
        Bitmap pic = ImageUtility.retrieveImage("testImages/Gralise");
        assertThat(pic, instanceOf(Bitmap.class));

        DetectionData dd = mDetector.processImage(convertBitmap(pic), 1, true);

        ImageUtility.storeImage(dd.getSelectedPill(), "/testImages/results/Gralise");

        File dir = Environment.getExternalStorageDirectory();
        String fileToGet = dir.getPath() + "/Pictures/testImages/results/Gralise.jpg";
        File testFile = new File(fileToGet);

        assertTrue(testFile.exists());
        assertTrue(dd.getSelectedPills().size() > 0);
    }

    /*
     * This test is for Imuran pills.  The user should expect light yellow figure 8 pills.
     * Same red dots.
     */
    @Test
    public void ImuranTest() {
        Bitmap pic = ImageUtility.retrieveImage("testImages/Imuran");
        assertThat(pic, instanceOf(Bitmap.class));

        DetectionData dd = mDetector.processImage(convertBitmap(pic), 1, true);

        ImageUtility.storeImage(dd.getSelectedPill(), "/testImages/results/Imuran");

        File dir = Environment.getExternalStorageDirectory();
        String fileToGet = dir.getPath() + "/Pictures/testImages/results/Imuran.jpg";
        File testFile = new File(fileToGet);

        assertTrue(testFile.exists());
        assertTrue(dd.getSelectedPills().size() > 0);
    }

    /*
     * This test is for Irbesartan pills.  The user should expect white long ellipse pills.
     * Same red dots.
     */
    @Test
    public void IrbesartanTest() {
        Bitmap pic = ImageUtility.retrieveImage("testImages/Irbesartan");
        assertThat(pic, instanceOf(Bitmap.class));

        DetectionData dd = mDetector.processImage(convertBitmap(pic), 1, true);

        ImageUtility.storeImage(dd.getSelectedPill(), "/testImages/results/Irbesartan");

        File dir = Environment.getExternalStorageDirectory();
        String fileToGet = dir.getPath() + "/Pictures/testImages/results/Irbesartan.jpg";
        File testFile = new File(fileToGet);

        assertTrue(testFile.exists());
        assertTrue(dd.getSelectedPills().size() > 0);
    }

    /*
     * This test is for Klonopin_hole pills.  The user should expect blue circle with K hole pills.
     * Same red dots.
     */
    @Test
    public void KlonopinHoleTest() {
        Bitmap pic = ImageUtility.retrieveImage("testImages/Klonopin_hole");
        assertThat(pic, instanceOf(Bitmap.class));

        DetectionData dd = mDetector.processImage(convertBitmap(pic), 1, true);

        ImageUtility.storeImage(dd.getSelectedPill(), "/testImages/results/Klonopin_hole");

        File dir = Environment.getExternalStorageDirectory();
        String fileToGet = dir.getPath() + "/Pictures/testImages/results/Klonopin_hole.jpg";
        File testFile = new File(fileToGet);

        assertTrue(testFile.exists());
        assertTrue(dd.getSelectedPills().size() > 0);
    }

    /*
     * This test is for Lamictal pills.  The user should expect white tiny rounded square pills.
     * Same red dots.
     */
    @Test
    public void LamictalTest() {
        Bitmap pic = ImageUtility.retrieveImage("testImages/Lamictal");
        assertThat(pic, instanceOf(Bitmap.class));

        DetectionData dd = mDetector.processImage(convertBitmap(pic), 1, true);

        ImageUtility.storeImage(dd.getSelectedPill(), "/testImages/results/Lamictal");

        File dir = Environment.getExternalStorageDirectory();
        String fileToGet = dir.getPath() + "/Pictures/testImages/results/Lamictal.jpg";
        File testFile = new File(fileToGet);

        assertTrue(testFile.exists());
        assertTrue(dd.getSelectedPills().size() > 0);
    }

    /*
     * This test is for Lamictal_shield pills.  The user should expect pinkish orange shield shaped pills.
     * Same red dots.
     */
    @Test
    public void LamictalShieldTest() {
        Bitmap pic = ImageUtility.retrieveImage("testImages/Lamictal_shield");
        assertThat(pic, instanceOf(Bitmap.class));

        DetectionData dd = mDetector.processImage(convertBitmap(pic), 1, true);

        ImageUtility.storeImage(dd.getSelectedPill(), "/testImages/results/Lamictal_shield");

        File dir = Environment.getExternalStorageDirectory();
        String fileToGet = dir.getPath() + "/Pictures/testImages/results/Lamictal_shield.jpg";
        File testFile = new File(fileToGet);

        assertTrue(testFile.exists());
        assertTrue(dd.getSelectedPills().size() > 0);
    }

    /*
     * This test is for Lamictal_XR pills.  The user should expect brown with white center circle pills.
     * Same red dots.
     */
    @Test
    public void LamictalXRTest() {
        Bitmap pic = ImageUtility.retrieveImage("testImages/Lamictal_XR");
        assertThat(pic, instanceOf(Bitmap.class));

        DetectionData dd = mDetector.processImage(convertBitmap(pic), 1, true);

        ImageUtility.storeImage(dd.getSelectedPill(), "/testImages/results/Lamictal_XR");

        File dir = Environment.getExternalStorageDirectory();
        String fileToGet = dir.getPath() + "/Pictures/testImages/results/Lamictal_XR.jpg";
        File testFile = new File(fileToGet);

        assertTrue(testFile.exists());
        assertTrue(dd.getSelectedPills().size() > 0);
    }

    /*
     * This test is for Lamictal_XR_tall pills.  The user should expect grey oblong with white center pills.
     * Same red dots.
     */
    @Test
    public void LamictalXRTallTest() {
        Bitmap pic = ImageUtility.retrieveImage("testImages/Lamictal_XR_tall");
        assertThat(pic, instanceOf(Bitmap.class));

        DetectionData dd = mDetector.processImage(convertBitmap(pic), 1, true);

        ImageUtility.storeImage(dd.getSelectedPill(), "/testImages/results/Lamictal_XR_tall");

        File dir = Environment.getExternalStorageDirectory();
        String fileToGet = dir.getPath() + "/Pictures/testImages/results/Lamictal_XR_tall.jpg";
        File testFile = new File(fileToGet);

        assertTrue(testFile.exists());
        assertTrue(dd.getSelectedPills().size() > 0);
    }

    /*
     * This test is for Lamotrigine pills.  The user should expect white tiny shield pills.
     * Same red dots.
     */
    @Test
    public void LamotrigineTest() {
        Bitmap pic = ImageUtility.retrieveImage("testImages/Lamotrigine");
        assertThat(pic, instanceOf(Bitmap.class));

        DetectionData dd = mDetector.processImage(convertBitmap(pic), 1, true);

        ImageUtility.storeImage(dd.getSelectedPill(), "/testImages/results/Lamotrigine");

        File dir = Environment.getExternalStorageDirectory();
        String fileToGet = dir.getPath() + "/Pictures/testImages/results/Lamotrigine.jpg";
        File testFile = new File(fileToGet);

        assertTrue(testFile.exists());
        assertTrue(dd.getSelectedPills().size() > 0);
    }

    /*
     * This test is for Lamotrigine_halves pills.  The user should expect white larger dented triangle pills.
     * Same red dots.
     */
    @Test
    public void LamotrigineHalvesTest() {
        Bitmap pic = ImageUtility.retrieveImage("testImages/Lamotrigine_halves");
        assertThat(pic, instanceOf(Bitmap.class));

        DetectionData dd = mDetector.processImage(convertBitmap(pic), 1, true);

        ImageUtility.storeImage(dd.getSelectedPill(), "/testImages/results/Lamotrigine_halves");

        File dir = Environment.getExternalStorageDirectory();
        String fileToGet = dir.getPath() + "/Pictures/testImages/results/Lamotrigine_halves.jpg";
        File testFile = new File(fileToGet);

        assertTrue(testFile.exists());
        assertTrue(dd.getSelectedPills().size() > 0);
    }

    /*
     * This test is for Lamotrigine_thick pills.  The user should expect tiny white rounded square pills.
     * Same red dots.
     */
    @Test
    public void LamotrigineThickTest() {
        Bitmap pic = ImageUtility.retrieveImage("testImages/Lamotrigine_thick");
        assertThat(pic, instanceOf(Bitmap.class));

        DetectionData dd = mDetector.processImage(convertBitmap(pic), 1, true);

        ImageUtility.storeImage(dd.getSelectedPill(), "/testImages/results/Lamotrigine_thick");

        File dir = Environment.getExternalStorageDirectory();
        String fileToGet = dir.getPath() + "/Pictures/testImages/results/Lamotrigine_thick.jpg";
        File testFile = new File(fileToGet);

        assertTrue(testFile.exists());
        assertTrue(dd.getSelectedPills().size() > 0);
    }

    /*
     * This test is for Lamotrigine-1 pills.  The user should expect white small pentagon pills.
     * Same red dots.
     */
    @Test
    public void LamotrigineOneTest() {
        Bitmap pic = ImageUtility.retrieveImage("testImages/Lamotrigine-1");
        assertThat(pic, instanceOf(Bitmap.class));

        DetectionData dd = mDetector.processImage(convertBitmap(pic), 1, true);

        ImageUtility.storeImage(dd.getSelectedPill(), "/testImages/results/Lamotrigine-1");

        File dir = Environment.getExternalStorageDirectory();
        String fileToGet = dir.getPath() + "/Pictures/testImages/results/Lamotrigine-1.jpg";
        File testFile = new File(fileToGet);

        assertTrue(testFile.exists());
        assertTrue(dd.getSelectedPills().size() > 0);
    }

    /*
     * This test is for Lasix pills.  The user should expect white circle pills with prism cut edges pills.
     * Same red dots.
     */
    @Test
    public void LasixTest() {
        Bitmap pic = ImageUtility.retrieveImage("testImages/Lasix");
        assertThat(pic, instanceOf(Bitmap.class));

        DetectionData dd = mDetector.processImage(convertBitmap(pic), 1, true);

        ImageUtility.storeImage(dd.getSelectedPill(), "/testImages/results/Lasix");

        File dir = Environment.getExternalStorageDirectory();
        String fileToGet = dir.getPath() + "/Pictures/testImages/results/Lasix.jpg";
        File testFile = new File(fileToGet);

        assertTrue(testFile.exists());
        assertTrue(dd.getSelectedPills().size() > 0);
    }

    /*
     * This test is for Levoxyl pills.  The user should expect dented oblong ellipse yellow pills.
     * Same red dots.
     */
    @Test
    public void LevoxylTest() {
        Bitmap pic = ImageUtility.retrieveImage("testImages/Levoxyl");
        assertThat(pic, instanceOf(Bitmap.class));

        DetectionData dd = mDetector.processImage(convertBitmap(pic), 1, true);

        ImageUtility.storeImage(dd.getSelectedPill(), "/testImages/results/Levoxyl");

        File dir = Environment.getExternalStorageDirectory();
        String fileToGet = dir.getPath() + "/Pictures/testImages/results/Levoxyl.jpg";
        File testFile = new File(fileToGet);

        assertTrue(testFile.exists());
        assertTrue(dd.getSelectedPills().size() > 0);
    }

    /*
     * This test is for Norel_AD pills.  The user should expect triangle half white half yellow pills.
     * Same red dots.
     */
    @Test
    public void NorelADTest() {
        Bitmap pic = ImageUtility.retrieveImage("testImages/Norel_AD");
        assertThat(pic, instanceOf(Bitmap.class));

        DetectionData dd = mDetector.processImage(convertBitmap(pic), 1, true);

        ImageUtility.storeImage(dd.getSelectedPill(), "/testImages/results/Norel_AD");

        File dir = Environment.getExternalStorageDirectory();
        String fileToGet = dir.getPath() + "/Pictures/testImages/results/Norel_AD.jpg";
        File testFile = new File(fileToGet);

        assertTrue(testFile.exists());
        assertTrue(dd.getSelectedPills().size() > 0);
    }

    /*
     * This test is for Norvasc pills.  The user should expect white diamond pills.
     * Same red dots.
     */
    @Test
    public void NorvascTest() {
        Bitmap pic = ImageUtility.retrieveImage("testImages/Norvasc");
        assertThat(pic, instanceOf(Bitmap.class));

        DetectionData dd = mDetector.processImage(convertBitmap(pic), 1, true);

        ImageUtility.storeImage(dd.getSelectedPill(), "/testImages/results/Norvasc");

        File dir = Environment.getExternalStorageDirectory();
        String fileToGet = dir.getPath() + "/Pictures/testImages/results/Norvasc.jpg";
        File testFile = new File(fileToGet);

        assertTrue(testFile.exists());
        assertTrue(dd.getSelectedPills().size() > 0);
    }

    /*
     * This test is for Norvasc_5mg pills.  The user should expect white chopped corner rectangle pills.
     * Same red dots.
     */
    @Test
    public void NorvascFiveMgTest() {
        Bitmap pic = ImageUtility.retrieveImage("testImages/Norvasc_5mg");
        assertThat(pic, instanceOf(Bitmap.class));

        DetectionData dd = mDetector.processImage(convertBitmap(pic), 1, true);

        ImageUtility.storeImage(dd.getSelectedPill(), "/testImages/results/Norvasc_5mg");

        File dir = Environment.getExternalStorageDirectory();
        String fileToGet = dir.getPath() + "/Pictures/testImages/results/Norvasc_5mg.jpg";
        File testFile = new File(fileToGet);

        assertTrue(testFile.exists());
        assertTrue(dd.getSelectedPills().size() > 0);
    }

    /*
     * This test is for Nuvigil pills.  The user should expect shiny white elliptical pills.
     * Same red dots.
     */
    @Test
    public void NuvigilTest() {
        Bitmap pic = ImageUtility.retrieveImage("testImages/Nuvigil");
        assertThat(pic, instanceOf(Bitmap.class));

        DetectionData dd = mDetector.processImage(convertBitmap(pic), 1, true);

        ImageUtility.storeImage(dd.getSelectedPill(), "/testImages/results/Nuvigil");

        File dir = Environment.getExternalStorageDirectory();
        String fileToGet = dir.getPath() + "/Pictures/testImages/results/Nuvigil.jpg";
        File testFile = new File(fileToGet);

        assertTrue(testFile.exists());
        assertTrue(dd.getSelectedPills().size() > 0);
    }

    /*
     * This test is for Otezla pills.  The user should expect pink two rounded corner diamond pills.
     * Same red dots.
     */
    @Test
    public void OtezlaTest() {
        Bitmap pic = ImageUtility.retrieveImage("testImages/Otezla");
        assertThat(pic, instanceOf(Bitmap.class));

        DetectionData dd = mDetector.processImage(convertBitmap(pic), 1, true);

        ImageUtility.storeImage(dd.getSelectedPill(), "/testImages/results/Otezla");

        File dir = Environment.getExternalStorageDirectory();
        String fileToGet = dir.getPath() + "/Pictures/testImages/results/Otezla.jpg";
        File testFile = new File(fileToGet);

        assertTrue(testFile.exists());
        assertTrue(dd.getSelectedPills().size() > 0);
    }

    /*
     * This test is for Pepcid pills.  The user should expect yellow half-rounded square pills.
     * Same red dots.
     */
    @Test
    public void PepcidTest() {
        Bitmap pic = ImageUtility.retrieveImage("testImages/Pepcid");
        assertThat(pic, instanceOf(Bitmap.class));

        DetectionData dd = mDetector.processImage(convertBitmap(pic), 1, true);

        ImageUtility.storeImage(dd.getSelectedPill(), "/testImages/results/Pepcid");

        File dir = Environment.getExternalStorageDirectory();
        String fileToGet = dir.getPath() + "/Pictures/testImages/results/Pepcid.jpg";
        File testFile = new File(fileToGet);

        assertTrue(testFile.exists());
        assertTrue(dd.getSelectedPills().size() > 0);
    }

    /*
     * This test is for Plaquenil pills.  The user should expect shiny white figure eight pills with black writing.
     * Same red dots.
     */
    @Test
    public void PlaquenilTest() {
        Bitmap pic = ImageUtility.retrieveImage("testImages/Plaquenil");
        assertThat(pic, instanceOf(Bitmap.class));

        DetectionData dd = mDetector.processImage(convertBitmap(pic), 1, true);

        ImageUtility.storeImage(dd.getSelectedPill(), "/testImages/results/Plaquenil");

        File dir = Environment.getExternalStorageDirectory();
        String fileToGet = dir.getPath() + "/Pictures/testImages/results/Plaquenil.jpg";
        File testFile = new File(fileToGet);

        assertTrue(testFile.exists());
        assertTrue(dd.getSelectedPills().size() > 0);
    }

    /*
     * This test is for Prinivil pills.  The user should expect white top chopped triangle pills.
     * Same red dots.
     */
    @Test
    public void PrinivilTest() {
        Bitmap pic = ImageUtility.retrieveImage("testImages/Prinivil");
        assertThat(pic, instanceOf(Bitmap.class));

        DetectionData dd = mDetector.processImage(convertBitmap(pic), 1, true);

        ImageUtility.storeImage(dd.getSelectedPill(), "/testImages/results/Prinivil");

        File dir = Environment.getExternalStorageDirectory();
        String fileToGet = dir.getPath() + "/Pictures/testImages/results/Prinivil.jpg";
        File testFile = new File(fileToGet);

        assertTrue(testFile.exists());
        assertTrue(dd.getSelectedPills().size() > 0);
    }

    /*
     * This test is for Prinivil_10mg pills.  The user should expect yellow top chopped triangle pills.
     * Same red dots.
     */
    @Test
    public void PrinivilTenMgTest() {
        Bitmap pic = ImageUtility.retrieveImage("testImages/Prinivil_10mg");
        assertThat(pic, instanceOf(Bitmap.class));

        DetectionData dd = mDetector.processImage(convertBitmap(pic), 1, true);

        ImageUtility.storeImage(dd.getSelectedPill(), "/testImages/results/Prinivil_10mg");

        File dir = Environment.getExternalStorageDirectory();
        String fileToGet = dir.getPath() + "/Pictures/testImages/results/Prinivil_10mg.jpg";
        File testFile = new File(fileToGet);

        assertTrue(testFile.exists());
        assertTrue(dd.getSelectedPills().size() > 0);
    }

    /*
     * This test is for Propecia pills.  The user should expect bright pink octogonal pills.
     * Same red dots.
     */
    @Test
    public void PropeciaTest() {
        Bitmap pic = ImageUtility.retrieveImage("testImages/Propecia");
        assertThat(pic, instanceOf(Bitmap.class));

        DetectionData dd = mDetector.processImage(convertBitmap(pic), 1, true);

        ImageUtility.storeImage(dd.getSelectedPill(), "/testImages/results/Propecia");

        File dir = Environment.getExternalStorageDirectory();
        String fileToGet = dir.getPath() + "/Pictures/testImages/results/Propecia.jpg";
        File testFile = new File(fileToGet);

        assertTrue(testFile.exists());
        assertTrue(dd.getSelectedPills().size() > 0);
    }

    /*
     * This test is for Proscar pills.  The user should expect small blue egg shaped pills.
     * Same red dots.
     */
    @Test
    public void ProscarTest() {
        Bitmap pic = ImageUtility.retrieveImage("testImages/Proscar");
        assertThat(pic, instanceOf(Bitmap.class));

        DetectionData dd = mDetector.processImage(convertBitmap(pic), 1, true);

        ImageUtility.storeImage(dd.getSelectedPill(), "/testImages/results/Proscar");

        File dir = Environment.getExternalStorageDirectory();
        String fileToGet = dir.getPath() + "/Pictures/testImages/results/Proscar.jpg";
        File testFile = new File(fileToGet);

        assertTrue(testFile.exists());
        assertTrue(dd.getSelectedPills().size() > 0);
    }

    /*
     * This test is for Rolaids pills.  The user should expect large white circular with and R pills.
     * Same red dots.
     */
    @Test
    public void RolaidsTest() {
        Bitmap pic = ImageUtility.retrieveImage("testImages/Rolaids");
        assertThat(pic, instanceOf(Bitmap.class));

        DetectionData dd = mDetector.processImage(convertBitmap(pic), 1, true);

        ImageUtility.storeImage(dd.getSelectedPill(), "/testImages/results/Rolaids");

        File dir = Environment.getExternalStorageDirectory();
        String fileToGet = dir.getPath() + "/Pictures/testImages/results/Rolaids.jpg";
        File testFile = new File(fileToGet);

        assertTrue(testFile.exists());
        assertTrue(dd.getSelectedPills().size() > 0);
    }

    /*
     * This test is for Saphris pills.  The user should expect large white circular with a S pills.
     * Same red dots.
     */
    @Test
    public void SaphrisTest() {
        Bitmap pic = ImageUtility.retrieveImage("testImages/Saphris");
        assertThat(pic, instanceOf(Bitmap.class));

        DetectionData dd = mDetector.processImage(convertBitmap(pic), 1, true);

        ImageUtility.storeImage(dd.getSelectedPill(), "/testImages/results/Saphris");

        File dir = Environment.getExternalStorageDirectory();
        String fileToGet = dir.getPath() + "/Pictures/testImages/results/Saphris.jpg";
        File testFile = new File(fileToGet);

        assertTrue(testFile.exists());
        assertTrue(dd.getSelectedPills().size() > 0);
    }

    /*
     * This test is for Spritam pills.  The user should expect large white circular with a < . pills.
     * Same red dots.
     */
    @Test
    public void SpritamTest() {
        Bitmap pic = ImageUtility.retrieveImage("testImages/Spritam");
        assertThat(pic, instanceOf(Bitmap.class));

        DetectionData dd = mDetector.processImage(convertBitmap(pic), 1, true);

        ImageUtility.storeImage(dd.getSelectedPill(), "/testImages/results/Spritam");

        File dir = Environment.getExternalStorageDirectory();
        String fileToGet = dir.getPath() + "/Pictures/testImages/results/Spritam.jpg";
        File testFile = new File(fileToGet);

        assertTrue(testFile.exists());
        assertTrue(dd.getSelectedPills().size() > 0);
    }

    /*
     * This test is for Symax_Duotab pills.  The user should expect half blue, half white with DUOTAB pills.
     * Same red dots.
     */
    @Test
    public void SymaxDuotabTest() {
        Bitmap pic = ImageUtility.retrieveImage("testImages/Symax_Duotab");
        assertThat(pic, instanceOf(Bitmap.class));

        DetectionData dd = mDetector.processImage(convertBitmap(pic), 1, true);

        ImageUtility.storeImage(dd.getSelectedPill(), "/testImages/results/Symax_Duotab");

        File dir = Environment.getExternalStorageDirectory();
        String fileToGet = dir.getPath() + "/Pictures/testImages/results/Symax_Duotab.jpg";
        File testFile = new File(fileToGet);

        assertTrue(testFile.exists());
        assertTrue(dd.getSelectedPills().size() > 0);
    }

    /*
     * This test is for Tenex pills.  The user should expect light pink diamonds with TENEX pills.
     * Same red dots.
     */
    @Test
    public void TenexTest() {
        Bitmap pic = ImageUtility.retrieveImage("testImages/Tenex");
        assertThat(pic, instanceOf(Bitmap.class));

        DetectionData dd = mDetector.processImage(convertBitmap(pic), 1, true);

        ImageUtility.storeImage(dd.getSelectedPill(), "/testImages/results/Tenex");

        File dir = Environment.getExternalStorageDirectory();
        String fileToGet = dir.getPath() + "/Pictures/testImages/results/Tenex.jpg";
        File testFile = new File(fileToGet);

        assertTrue(testFile.exists());
        assertTrue(dd.getSelectedPills().size() > 0);
    }

    /*
     * This test is for Tiumeq pills.  The user should expect large purple elliptical pills.
     * Same red dots.
     */
    @Test
    public void TiumeqTest() {
        Bitmap pic = ImageUtility.retrieveImage("testImages/Tiumeq");
        assertThat(pic, instanceOf(Bitmap.class));

        DetectionData dd = mDetector.processImage(convertBitmap(pic), 1, true);

        ImageUtility.storeImage(dd.getSelectedPill(), "/testImages/results/Tiumeq");

        File dir = Environment.getExternalStorageDirectory();
        String fileToGet = dir.getPath() + "/Pictures/testImages/results/Tiumeq.jpg";
        File testFile = new File(fileToGet);

        assertTrue(testFile.exists());
        assertTrue(dd.getSelectedPills().size() > 0);
    }

    /*
     * This test is for Tranxene pills.  The user should expect yellow sheild with OV32 pills.
     * Same red dots.
     */
    @Test
    public void TranxeneTest() {
        Bitmap pic = ImageUtility.retrieveImage("testImages/Tranxene");
        assertThat(pic, instanceOf(Bitmap.class));

        DetectionData dd = mDetector.processImage(convertBitmap(pic), 1, true);

        ImageUtility.storeImage(dd.getSelectedPill(), "/testImages/results/Tranxene");

        File dir = Environment.getExternalStorageDirectory();
        String fileToGet = dir.getPath() + "/Pictures/testImages/results/Tranxene.jpg";
        File testFile = new File(fileToGet);

        assertTrue(testFile.exists());
        assertTrue(dd.getSelectedPills().size() > 0);
    }

    /*
     * This test is for Tylenol_500mg pills.  The user should expect white capsules with tylonel 500 in red pills.
     * Same red dots.
     */
    @Test
    public void TylenolFiveHundredMgTest() {
        Bitmap pic = ImageUtility.retrieveImage("testImages/Tylenol_500mg");
        assertThat(pic, instanceOf(Bitmap.class));

        DetectionData dd = mDetector.processImage(convertBitmap(pic), 1, true);

        ImageUtility.storeImage(dd.getSelectedPill(), "/testImages/results/Tylenol_500mg");

        File dir = Environment.getExternalStorageDirectory();
        String fileToGet = dir.getPath() + "/Pictures/testImages/results/Tylenol_500mg.jpg";
        File testFile = new File(fileToGet);

        assertTrue(testFile.exists());
        assertTrue(dd.getSelectedPills().size() > 0);
    }

    /*
     * This test is for Uloric pills.  The user should expect dark green egg shaped pills.
     * Same red dots.
     */
    @Test
    public void UloricTest() {
        Bitmap pic = ImageUtility.retrieveImage("testImages/Uloric");
        assertThat(pic, instanceOf(Bitmap.class));

        DetectionData dd = mDetector.processImage(convertBitmap(pic), 1, true);

        ImageUtility.storeImage(dd.getSelectedPill(), "/testImages/results/Uloric");

        File dir = Environment.getExternalStorageDirectory();
        String fileToGet = dir.getPath() + "/Pictures/testImages/results/Uloric.jpg";
        File testFile = new File(fileToGet);

        assertTrue(testFile.exists());
        assertTrue(dd.getSelectedPills().size() > 0);
    }

    /*
     * This test is for Valium_hole pills.  The user should expect a white circular pill with V hole.
     * Same red dots.
     */
    @Test
    public void ValiumHoleTest() {
        Bitmap pic = ImageUtility.retrieveImage("testImages/Valium_hole");
        assertThat(pic, instanceOf(Bitmap.class));

        DetectionData dd = mDetector.processImage(convertBitmap(pic), 1, true);

        ImageUtility.storeImage(dd.getSelectedPill(), "/testImages/results/Valium_hole");

        File dir = Environment.getExternalStorageDirectory();
        String fileToGet = dir.getPath() + "/Pictures/testImages/results/Valium_hole.jpg";
        File testFile = new File(fileToGet);

        assertTrue(testFile.exists());
        assertTrue(dd.getSelectedPills().size() > 0);
    }

    /*
     * This test is for Vaseretic pills.  The user should expect dark red extended hexagon pills.
     * Same red dots.
     */
    @Test
    public void VasereticTest() {
        Bitmap pic = ImageUtility.retrieveImage("testImages/Vaseretic");
        assertThat(pic, instanceOf(Bitmap.class));

        DetectionData dd = mDetector.processImage(convertBitmap(pic), 1, true);

        ImageUtility.storeImage(dd.getSelectedPill(), "/testImages/results/Vaseretic");

        File dir = Environment.getExternalStorageDirectory();
        String fileToGet = dir.getPath() + "/Pictures/testImages/results/Vaseretic.jpg";
        File testFile = new File(fileToGet);

        assertTrue(testFile.exists());
        assertTrue(dd.getSelectedPills().size() > 0);
    }

    /*
     * This test is for Vasotec pills.  The user should expect white flat two large rounded sides square pills.
     * Same red dots.
     */
    @Test
    public void VasotecTest() {
        Bitmap pic = ImageUtility.retrieveImage("testImages/Vasotec");
        assertThat(pic, instanceOf(Bitmap.class));

        DetectionData dd = mDetector.processImage(convertBitmap(pic), 1, true);

        ImageUtility.storeImage(dd.getSelectedPill(), "/testImages/results/Vasotec");

        File dir = Environment.getExternalStorageDirectory();
        String fileToGet = dir.getPath() + "/Pictures/testImages/results/Vasotec.jpg";
        File testFile = new File(fileToGet);

        assertTrue(testFile.exists());
        assertTrue(dd.getSelectedPills().size() > 0);
    }

    /*
     * This test is for Xanax_1mg pills.  The user should expect light yellow square with an X pills.
     * Same red dots.
     */
    @Test
    public void XanaxOneMgTest() {
        Bitmap pic = ImageUtility.retrieveImage("testImages/Xanax_1mg");
        assertThat(pic, instanceOf(Bitmap.class));

        DetectionData dd = mDetector.processImage(convertBitmap(pic), 1, true);

        ImageUtility.storeImage(dd.getSelectedPill(), "/testImages/results/Xanax_1mg");

        File dir = Environment.getExternalStorageDirectory();
        String fileToGet = dir.getPath() + "/Pictures/testImages/results/Xanax_1mg.jpg";
        File testFile = new File(fileToGet);

        assertTrue(testFile.exists());
        assertTrue(dd.getSelectedPills().size() > 0);
    }

    /*
     * This test is for Xanax_1mg_cut pills.  The user should expect light blue oval with a cut line pills.
     * Same red dots.
     */
    @Test
    public void XanaxOneMgCutTest() {
        Bitmap pic = ImageUtility.retrieveImage("testImages/Xanax_1mg_cut");
        assertThat(pic, instanceOf(Bitmap.class));

        DetectionData dd = mDetector.processImage(convertBitmap(pic), 1, true);

        ImageUtility.storeImage(dd.getSelectedPill(), "/testImages/results/Xanax_1mg_cut");

        File dir = Environment.getExternalStorageDirectory();
        String fileToGet = dir.getPath() + "/Pictures/testImages/results/Xanax_1mg_cut.jpg";
        File testFile = new File(fileToGet);

        assertTrue(testFile.exists());
        assertTrue(dd.getSelectedPills().size() > 0);
    }

    /*
     * This test is for Xanax_2mg pills.  The user should expect white round with a 2 pills.
     * Same red dots.
     */
    @Test
    public void XanaxTwoMgTest() {
        Bitmap pic = ImageUtility.retrieveImage("testImages/Xanax_2mg");
        assertThat(pic, instanceOf(Bitmap.class));

        DetectionData dd = mDetector.processImage(convertBitmap(pic), 1, true);

        ImageUtility.storeImage(dd.getSelectedPill(), "/testImages/results/Xanax_2mg");

        File dir = Environment.getExternalStorageDirectory();
        String fileToGet = dir.getPath() + "/Pictures/testImages/results/Xanax_2mg.jpg";
        File testFile = new File(fileToGet);

        assertTrue(testFile.exists());
        assertTrue(dd.getSelectedPills().size() > 0);
    }

    /*
     * This test is for Xanax_2mg_cut pills.  The user should expect white white rectangle with 4 partition pills.
     * Same red dots.
     */
    @Test
    public void XanaxTwoMgCutTest() {
        Bitmap pic = ImageUtility.retrieveImage("testImages/Xanax_2mg_cut");
        assertThat(pic, instanceOf(Bitmap.class));

        DetectionData dd = mDetector.processImage(convertBitmap(pic), 1, true);

        ImageUtility.storeImage(dd.getSelectedPill(), "/testImages/results/Xanax_2mg_cut");

        File dir = Environment.getExternalStorageDirectory();
        String fileToGet = dir.getPath() + "/Pictures/testImages/results/Xanax_2mg_cut.jpg";
        File testFile = new File(fileToGet);

        assertTrue(testFile.exists());
        assertTrue(dd.getSelectedPills().size() > 0);
    }

    /*
     * This test is for Xanax_3mg pills.  The user should expect pale yellow triangles with an X pills.
     * Same red dots.
     */
    @Test
    public void XanaxThreeMgTest() {
        Bitmap pic = ImageUtility.retrieveImage("testImages/Xanax_3mg");
        assertThat(pic, instanceOf(Bitmap.class));

        DetectionData dd = mDetector.processImage(convertBitmap(pic), 1, true);

        ImageUtility.storeImage(dd.getSelectedPill(), "/testImages/results/Xanax_3mg");

        File dir = Environment.getExternalStorageDirectory();
        String fileToGet = dir.getPath() + "/Pictures/testImages/results/Xanax_3mg.jpg";
        File testFile = new File(fileToGet);

        assertTrue(testFile.exists());
        assertTrue(dd.getSelectedPills().size() > 0);
    }

    /*
     * This test is for Xanax_5mg pills.  The user should expect pale pink pentagon with a 0.5 pills.
     * Same red dots.
     */
    @Test
    public void XanaxFiveMgTest() {
        Bitmap pic = ImageUtility.retrieveImage("testImages/Xanax_5mg");
        assertThat(pic, instanceOf(Bitmap.class));

        DetectionData dd = mDetector.processImage(convertBitmap(pic), 1, true);

        ImageUtility.storeImage(dd.getSelectedPill(), "/testImages/results/Xanax_5mg");

        File dir = Environment.getExternalStorageDirectory();
        String fileToGet = dir.getPath() + "/Pictures/testImages/results/Xanax_5mg.jpg";
        File testFile = new File(fileToGet);

        assertTrue(testFile.exists());
        assertTrue(dd.getSelectedPills().size() > 0);
    }

    /*
     * This test is for Zebeta_5mg pills.  The user should expect pale pink heart pills.
     * Same red dots.
     */
    @Test
    public void ZebetaFiveMgTest() {
        Bitmap pic = ImageUtility.retrieveImage("testImages/Zebeta_5mg");
        assertThat(pic, instanceOf(Bitmap.class));

        DetectionData dd = mDetector.processImage(convertBitmap(pic), 1, true);

        ImageUtility.storeImage(dd.getSelectedPill(), "/testImages/results/Zebeta_5mg");

        File dir = Environment.getExternalStorageDirectory();
        String fileToGet = dir.getPath() + "/Pictures/testImages/results/Zebeta_5mg.jpg";
        File testFile = new File(fileToGet);

        assertTrue(testFile.exists());
        assertTrue(dd.getSelectedPills().size() > 0);
    }

    /*
     * This test is for Zebeta_10mg pills.  The user should expect white heart pills.
     * Same red dots.
     */
    @Test
    public void ZebetaTenMgTest() {
        Bitmap pic = ImageUtility.retrieveImage("testImages/Zebeta_10mg");
        assertThat(pic, instanceOf(Bitmap.class));

        DetectionData dd = mDetector.processImage(convertBitmap(pic), 1, true);

        ImageUtility.storeImage(dd.getSelectedPill(), "/testImages/results/Zebeta_10mg");

        File dir = Environment.getExternalStorageDirectory();
        String fileToGet = dir.getPath() + "/Pictures/testImages/results/Zebeta_10mg.jpg";
        File testFile = new File(fileToGet);

        assertTrue(testFile.exists());
        assertTrue(dd.getSelectedPills().size() > 0);
    }

    /*
     * This test is for Ziagen pills.  The user should expect dark yellow rectangle pills.
     * Same red dots.
     */
    @Test
    public void ZiagenTest() {
        Bitmap pic = ImageUtility.retrieveImage("testImages/Ziagen");
        assertThat(pic, instanceOf(Bitmap.class));

        DetectionData dd = mDetector.processImage(convertBitmap(pic), 1, true);

        ImageUtility.storeImage(dd.getSelectedPill(), "/testImages/results/Ziagen");

        File dir = Environment.getExternalStorageDirectory();
        String fileToGet = dir.getPath() + "/Pictures/testImages/results/Ziagen.jpg";
        File testFile = new File(fileToGet);

        assertTrue(testFile.exists());
        assertTrue(dd.getSelectedPills().size() > 0);
    }

    /*
     * This test is for Zovirax pills.  The user should expect white shield with triangle pills.
     * Same red dots.
     */
    @Test
    public void ZoviraxTest() {
        Bitmap pic = ImageUtility.retrieveImage("testImages/Zovirax");
        assertThat(pic, instanceOf(Bitmap.class));

        DetectionData dd = mDetector.processImage(convertBitmap(pic), 1, true);

        ImageUtility.storeImage(dd.getSelectedPill(), "/testImages/results/Zovirax");

        File dir = Environment.getExternalStorageDirectory();
        String fileToGet = dir.getPath() + "/Pictures/testImages/results/Zovirax.jpg";
        File testFile = new File(fileToGet);

        assertTrue(testFile.exists());
        assertTrue(dd.getSelectedPills().size() > 0);
    }

    /*
     * This test is for Zubsolv pills.  The user should expect white short gravestone pills.
     * Same red dots.
     */
    @Test
    public void ZubsolvTest() {
        Bitmap pic = ImageUtility.retrieveImage("testImages/Zubsolv");
        assertThat(pic, instanceOf(Bitmap.class));

        DetectionData dd = mDetector.processImage(convertBitmap(pic), 1, true);

        ImageUtility.storeImage(dd.getSelectedPill(), "/testImages/results/Zubsolv");

        File dir = Environment.getExternalStorageDirectory();
        String fileToGet = dir.getPath() + "/Pictures/testImages/results/Zubsolv.jpg";
        File testFile = new File(fileToGet);

        assertTrue(testFile.exists());
        assertTrue(dd.getSelectedPills().size() > 0);
    }

    /*
     * This test is for Zydone pills.  The user should expect pale pink hexagonal capsule style pills.
     * Same red dots.
     */
    @Test
    public void ZydoneTest() {
        Bitmap pic = ImageUtility.retrieveImage("testImages/Zydone");
        assertThat(pic, instanceOf(Bitmap.class));

        DetectionData dd = mDetector.processImage(convertBitmap(pic), 1, true);

        ImageUtility.storeImage(dd.getSelectedPill(), "/testImages/results/Zydone");

        File dir = Environment.getExternalStorageDirectory();
        String fileToGet = dir.getPath() + "/Pictures/testImages/results/Zydone.jpg";
        File testFile = new File(fileToGet);

        assertTrue(testFile.exists());
        assertTrue(dd.getSelectedPills().size() > 0);
    }

    /*
     * This test is for Zytiga pills.  The user should expect White eillptical pills.
     * Same red dots.
     */
    @Test
    public void ZytigaTest() {
        Bitmap pic = ImageUtility.retrieveImage("testImages/Zytiga");
        assertThat(pic, instanceOf(Bitmap.class));

        DetectionData dd = mDetector.processImage(convertBitmap(pic), 1, true);

        ImageUtility.storeImage(dd.getSelectedPill(), "/testImages/results/Zytiga");

        File dir = Environment.getExternalStorageDirectory();
        String fileToGet = dir.getPath() + "/Pictures/testImages/results/Zytiga.jpg";
        File testFile = new File(fileToGet);

        assertTrue(testFile.exists());
        assertTrue(dd.getSelectedPills().size() > 0);
    }

    //***************************************************************************
    // Helper Methods
    //***************************************************************************
    private byte[] convertBitmap(Bitmap bmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

        return stream.toByteArray();
    }

}
