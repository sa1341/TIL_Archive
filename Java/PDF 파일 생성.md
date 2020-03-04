# Apache 라이브러리를 이용하여 PDF 파일을 만드는 예제

아래 예제는 아파치에서 제공해주는 PDDocument 객체를 이용해서 빈 pdf 파일을 생성하는 예제를 작성하였습니다.
사실 이 예제에 이미지 파일을 넣는 부분은 아직 못했는데 나중에 다시 이미지가 포함된 pdf 파일 생성 예제를 포스팅하겠습니다.


```java

public class createPdfExample{
  public static void main(String[] args) throws Exception {

     String sourceDir, saveTargetDir, savePdfFileName;
     sourceDir = "C:/examplePdf/images";      // 이미지 파일이 들어있는 경로
     saveTargetDir = "C:/examplePdf/output";  // pdf 파일이 들어있는 타겟 경로
     savePdfFileName = "Htf_test01.hwp";      // 생성할 pdf 파일 명

     executeConvertToPdf(sourceDir, saveTargetDir, savePdfFileName);

  }
  
  public static void executeConvertToPdf(String sourceDir, String saveTargetDir, String savePdfFileName) throws Exception {
     System.out.println("Loading All image Files --");

     File dir = new File(sourceDir);
     // 디렉토리 내부에 해당 파일 확장자를 필터링 할때 사용하는 FileFilter 익명 구현 객체 생성
     File[] sourceFiles = dir.listFiles(new FileFilter() {
        @Override
        public boolean accept(File pathname) {
           return pathname.getName().contains("png");
        }
     });

    // Apache에서 제공해주는 PDDocument 객체를 이용해서 해당 파일명으로 빈 pdf 객체를 생성하였습니다.
     File pdfFile = createPdf(saveTargetDir, savePdfFileName);
  }

  public static File createPdf(String dirPath, String fileName) throws Exception { 

    // Directory Check 
    File dir = new File(dirPath); 
    if(!dir.exists()) { 
      dir.mkdir(); 
    } 

    if(!dir.isDirectory()) {
      throw new Exception("dirPath is not dir");
    } 
    // File Check
    File pdfFile = new File(dirPath + File.separator + fileName + ".pdf"); 

    if(pdfFile.exists()) { 
        pdfFile.delete(); 
     }

    PDDocument document = new PDDocument(); 
    document.save(pdfFile); 
    document.close(); 
    System.out.println("PDF created"); return pdfFile; 
  }
 }
```
