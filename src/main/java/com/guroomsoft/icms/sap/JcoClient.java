package com.guroomsoft.icms.sap;

import com.guroomsoft.icms.config.JcoConfig;
import com.sap.conn.jco.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.*;

/**
 * @FileName: JcoClient
 * @Date: 2024.02.27
 * @Author: Nam Sang Jin
 * @Description:
 *  RFC 호출을 위한 공용 클래스
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JcoClient {
    private final JcoConfig jcoConfig;
    private JCoRepository jcoRepository;
    private JCoDestination jcoDestination;

    private JCoFunction jcoFunction;

    /**
     * JcoClient 초기화
     */
    public void init() throws Exception
    {
        try {
            jcoDestination = jcoConfig.jcoDestination();
            jcoRepository = jcoDestination.getRepository();
            log.error("👉 initialize jco connection");
        } catch (Exception e) {
            log.error("👉 Failed to initialize JcoClient");
        }
    }

    /**
     * SAP 연결 확인
     * @return
     */
    public boolean isConnected()
    {
        try {
            jcoDestination.ping();
            log.error("👉 ping SAP Server");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * SAP에서 정의한 RFC함수를 얻는다.
     * @param functionName RFC함수
     * @return
     * @throws Exception
     */
    public void getFunction(String functionName) throws Exception
    {
        if (jcoRepository == null) {
            init();
        }

        try {
            jcoFunction = jcoRepository.getFunctionTemplate(functionName).getFunction();
            if (jcoFunction == null) {
                log.error("👉 호출  RFC :" + functionName);
                new RuntimeException("👉 Function not found in SAP.");
            }

            log.info("👉 성공 RFC :" + functionName);
        } catch(Exception ex) {
            log.error("👉 " + ex.getMessage());
            throw ex;
        }
    }

    /**
     *  SAP에서 정의한 RFC함수 실행하도록 호출
     * INPUT : ArrayList -> HashMap -> JCoTable 순으로 데이터를 처리한다.
     * OUTPUT : JCoTable -> HashMap -> ArrayList 순으로 데이터를 처리한다.
     * @return
     */
    private void execute()
    {
        try {
            JCoContext.begin(jcoDestination);
            jcoFunction.execute(jcoDestination);
            JCoContext.end(jcoDestination);
        } catch (JCoException e) {
            log.error("👉 " + e.getMessage());
        }
    }

    /**
     * RFC함수를 실행한다
     */
    public void runRunction()
    {
        this.execute();
    }

    /**
     * @Method Name : setImportParam
     * @Method 설명     : RFC의 일반적인 import에 값을 설정하는 메소
     * @param impParam
     */
    public void setImportParam(LinkedHashMap impParam)
    {
        JCoParameterList imports = jcoFunction.getImportParameterList();
        JCoListMetaData listMetaData = imports.getListMetaData();

        Set importKeySet = impParam.keySet();

        String metaName = null, metaType = null, hashName, hashValue;
        String s_structureName = null, s_structureType=null;
        int metaLength = 0;
        boolean metaIsOptional = false;
        boolean isStructure = false;

        for (int i=0; i < listMetaData.getFieldCount(); i++)
        {
            metaName = listMetaData.getName(i);
            metaLength = listMetaData.getLength(i);
            metaIsOptional = listMetaData.isOptional(i);
            metaType = listMetaData.getRecordTypeName(i);
            isStructure  = listMetaData.isStructure(i);

            log.debug(">>>> metaName :: {}", metaName);

            if (isStructure)
            {
                JCoStructure tInput = imports.getStructure(i);;
                JCoRecordMetaData recordMetaData = tInput.getRecordMetaData();

                log.info(recordMetaData.toString());

                for (int m=0; m< recordMetaData.getFieldCount(); m++)
                {
                    s_structureName = recordMetaData.getName(m);
                    s_structureType = recordMetaData.getTypeAsString(m);
                    metaLength = recordMetaData.getByteLength(m);
                    log.debug("s_structureName: {} / s_structureType: {} ", s_structureName, s_structureType);

                    List<String> values = (List<String>) impParam.get(metaName);
                    for(String value: values) {
                        tInput.setValue(s_structureName, value);
                    }
                    jcoFunction.getImportParameterList().setValue(metaName, tInput);
                }
            }
            else
            {
                for(Iterator iterator=importKeySet.iterator(); iterator.hasNext();)
                {
                    hashName = (String) iterator.next();
                    if (hashName.equals(metaName)) {
                        hashValue = (String) impParam.get(hashName);
                        log.debug("👉 Import : {} = {}", hashName, hashValue);
                        jcoFunction.getImportParameterList().setValue(hashName, hashValue);
                    }
                }
            }
        }
    }


    public LinkedHashMap<String, Object> getExportParam()
    {
        LinkedHashMap<String, Object> resultMap = new LinkedHashMap<>();

        JCoParameterList exportParams = jcoFunction.getExportParameterList();
        JCoListMetaData listMetaData = exportParams.getListMetaData();

        String metaName = null, hashName = null, hashValue = null;
        String expValue = null;
        int metaLength = 0, hashLength=0;
        boolean metaIsOptional = false;

        for(int i=0; i<listMetaData.getFieldCount(); i++) {
            metaName = listMetaData.getName(i);
            metaLength = listMetaData.getByteLength(i);
            metaIsOptional = listMetaData.isOptional(i);

            expValue = jcoFunction.getExportParameterList().getString(metaName);
            resultMap.put(metaName, expValue);
            log.debug("👉 Export Value {}", expValue);
        }

        return resultMap;
    }

    /**
     *
     * @param tableName
     * @return
     */
    public ArrayList getTable(String tableName)
    {
        /*
         * OUTPUT : JCoTable -> HashMap -> ArrayList 순으로 데이터를 처리한다.
         */
        ArrayList tableDataSet = new ArrayList();
        JCoTable outputTable = jcoFunction.getTableParameterList().getTable(tableName);
        if (outputTable != null) {
            log.debug("👉 Table Name : {}, Row Count : {} ", tableName, outputTable.getNumRows());
        } else {
            log.error("👉 Output table is null ");
        }

        if (outputTable.getNumRows() > 0) {
            log.debug("👉 Row Count : {} ", outputTable.getNumRows());

            do {
                HashMap mapResult = new HashMap();
                for(JCoFieldIterator i = outputTable.getFieldIterator(); i.hasNextField();) {
                    JCoField field = i.nextField();
                    mapResult.put(field.getName(), field.getString());

                    log.debug("👉 {} : {}", field.getName(), field.getString());
                }

                tableDataSet.add(mapResult);
            } while (outputTable.nextRow());
        } else {
            log.debug("👉 No data");
        }

        return tableDataSet;
    }

    /**
     * RFC의 Table Parameter를 설정(ArrayList)
     * @param tableName
     * @param tableListIn
     */
    public void setTableParam(String tableName, ArrayList tableListIn)
    {
        /*
         * INPUT : ArrayList -> HashMap -> JCoTable 순으로 데이터를 처리한다.
         */
        if (tableListIn == null) return;

        JCoTable inTable = jcoFunction.getTableParameterList().getTable(tableName);
        JCoRecordMetaData recordMetaData = inTable.getRecordMetaData();

        String metaName = null, hashName = null, hashValue = null;
        int metaLength = 0, hashLength = 0;

        for(int j=0; j< tableListIn.size(); ++j){
            HashMap mapInput = (HashMap) tableListIn.get(j);
            Iterator itInput = mapInput.keySet().iterator();
            inTable.appendRow();

            //웹에서 받은 추가 할 데이타 추출
            while (itInput.hasNext())
            {
                hashName = (String) itInput.next();
                // RFC Table의 meta정보 추출
                for(int i=0; i<recordMetaData.getFieldCount(); i++){
                    metaName = recordMetaData.getName(i);
                    metaLength = recordMetaData.getByteLength(i);
                    hashValue = (String)mapInput.get(hashName);
                    log.debug("👉 Field: {} length: {} offset : {}",   recordMetaData.getName(i), recordMetaData.getByteLength(i), recordMetaData.getByteOffset(i));

                    if (hashName.equals(metaName)) {
                        //RFC Table의 컬럼명과 웹화면에서 추출한 데이타명이 같을때 세팅
                        inTable.setValue(hashName, hashValue);
                        log.debug("👉 {} - TableParam[{}] : {}={}", tableName, j, hashName, hashValue);
                    }
                }
            }
        }
    }

    /**
     * RFC의 Table Parameter를 설정(LinkedHashMap)
     * @param tableName
     * @param tableListIn
     */
    public void setHashMapTableParam(String tableName, LinkedHashMap tableListIn)
    {
        /*
         * INPUT : HashMap -> JCoTable 순으로 데이터를 처리한다.
         */
        if (tableListIn == null) return;

        JCoTable tInput = jcoFunction.getTableParameterList().getTable(tableName);
        JCoRecordMetaData recordMetaData = tInput.getRecordMetaData();

        String metaName = null, hashName = "", hashValue="";
        int metaLength=0, hashLength=0;

        //웹에서 받은 추가 할 데이타 추출
        Iterator itInput = tableListIn.keySet().iterator();
        tInput.appendRow();

        while(itInput.hasNext())
        {
            String field = (String) itInput.next();
            // RFC Table의 meta정보 추출
            for (int i=0; i< recordMetaData.getFieldCount(); i++)
            {
                metaName = recordMetaData.getName(i);
                metaLength  = recordMetaData.getByteLength(i);
                log.debug("👉 Field: {} length {}  offset {}",
                        recordMetaData.getName(i),
                        recordMetaData.getByteLength(i),
                        recordMetaData.getByteOffset(i));

                if (field.equals(metaName)){
                    //RFC Table의 컬럼명과 웹화면에서 추출한 데이타명이 같을때 세팅
                    tInput.setValue(field, tableListIn.get(field));
                }
            }
        }
    }

    /**
     * @Method Name : setImportParamFile
     * @Method 설명     : ImportParam가 첨부파일을 전달하는 방식일때 사용하는 메소드
     * @Date        : 2011. 12. 21.
     * @Author      : Administrator
     * @수정 History :
     * @param al_tableListIn : 첨부파일의 정보를 담고 있는 ArrayList
     * @param folder_nm : 공통 첨부 폴더내에  로그인한 사용자의 개인 폴더명 (사원번호로 설정)
     */
    public void setImportParamFile(ArrayList al_tableListIn, String folder_nm)
    {

        JCoParameterList imports = jcoFunction.getImportParameterList();
        JCoListMetaData listMetaData=imports.getListMetaData();

        String s_metaName = null, s_hashName = null, s_hashValue=null, s_metaType=null;
        String s_structureName = null, s_structureType=null;
        int i_metaLen=0, i_hashLen=0;
        boolean b_metaOpt = false, b_isTable=false, b_isStructure=false;


        for (int k=0; k<al_tableListIn.size(); k++){
            HashMap mapInput = (HashMap) al_tableListIn.get(k); //웹에서 전달한 파일정보 추출
            Iterator itInput = mapInput.keySet().iterator();

            String s_attach_status = (String)mapInput.get("ATTACH_STATUS"); //첨부 상태 Type(dataset명은 항상 'ATTACH_STATUS') : C=신규/변경, D=삭제, R=읽기
            String s_fileName = (String)mapInput.get("FILE_NM");        //파일명 (파일명의 dataset명은   항상 'FILE_NM')

            if (s_attach_status.equals("C")){ //첨부 상태 Type이 C일때만 파일을 첨부해서 전달

                for (int i=0; i< listMetaData.getFieldCount(); i++){
                    s_metaName = listMetaData.getName(i);
                    i_metaLen  = listMetaData.getByteLength(i);
                    b_metaOpt  = listMetaData.isOptional(i);
                    s_metaType = listMetaData.getTypeAsString(i);
                    b_isTable  = listMetaData.isTable(i);
                    b_isStructure  = listMetaData.isStructure(i);

                    System.out.println("s_metaName:"+s_metaName+"/s_metaType :"+s_metaType);
                    log.debug("s_metaName: {} / s_metaType : {}", s_metaName, s_metaType);

                    if ( s_metaType.equals("XSTRING") )
                    {
                        log.debug("입력된 값  : {}", jcoFunction.getImportParameterList().getValue(s_metaName));

                        if ( jcoFunction.getImportParameterList().getValue(s_metaName) != null ) {
                            continue;
                        }

                        byte file_binary[] = getBinaryDataFromFile(folder_nm, s_fileName );
                        jcoFunction.getImportParameterList().setValue(s_metaName, file_binary);

                        log.debug("File-ImportParam[{}}] : {} / {}", i, s_metaName, file_binary);
                        break; //파일 전달이 성공했으면 다음 파일 저장을 위해
                    }

                }
            }
        }
    }

    /**
     * @Method Name : getBinaryDataFromFile
     * @Method 설명     : RFC로 전달할 첨부파일을 WAS의 임시 폴더에서 read하여 바이너리형태(byte)로 변경하여 return
     * @Date        : 2011. 12. 21.
     * @Author      : Administrator
     * @수정 History :
     * @param folder_nm
     * @param s_fileName
     * @return byte[]
     */
    public byte[] getBinaryDataFromFile(String folder_nm, String s_fileName)
    {
        // TODO 임시파일 저장 경로 설정 필요
        //String temp_attachfile_path = StringUtils.getPropetiesValue("temp_attachfile_path");
        String temp_attachfile_path = "";
        String savePath = temp_attachfile_path + folder_nm + File.separator;

        byte input[] = null;
        File inf = null;
        InputStream fis = null;
        try {
            inf = new File(savePath+s_fileName);
            System.out.println("첨부파일 경로+파일명: "+savePath+s_fileName);


            int len = 0; // ==> 읽어온 파일의 길이가 저장됨(size값과 같음)
            String str = null;

            fis = new FileInputStream(inf);
            int size = fis.available(); // 읽어 들일 수 있는 추정 바이트 수
            input = new byte[size]; // 읽어 들일 수 있는 size 만큼 byte 배열 생성


            while((len = fis.read(input) ) > -1){
                str = new String(input);
                //System.out.println("str:"+str);
            }

            fis.close();
        } catch (FileNotFoundException e1) {
            log.error(e1.getMessage());
        } catch (IOException e) {
            log.error(e.getMessage());
        }finally{
            inf.delete(); //파일을 전달한 이후에는 해당 파일을 삭제한다.
        }
        return input;
    }

    /**
     * @Method Name : getBinaryDataFromFile
     * @Method 설명     : RFC로 전달할 첨부파일을 WAS의 임시 폴더에서 read하여 바이너리형태(byte)로 변경하여 return
     * @Date        : 2011. 12. 21.
     * @Author      : Administrator
     * @수정 History :
     * @param folder_nm
     * @param s_fileName
     * @return byte[]
     */
    public boolean getFileFromBinaryData(String folder_nm, String s_fileName, byte file_binary[] )
    {

        // TODO 임시파일 저장 경로 설정 필요
        //String temp_attachfile_path = StringUtils.getPropetiesValue("temp_attachfile_path");
        String temp_attachfile_path = "";
        String savePath = temp_attachfile_path + folder_nm + File.separator;
        boolean b_return = false;

        File file = null;
        OutputStream out = null;
        try {

            file = new File(savePath);
            if(file.isDirectory()==false) file.mkdir();

            file = new File(savePath+s_fileName);
            if(file.isFile()==false) file.createNewFile();

            log.error("(생성)첨부파일 경로+파일명: {}", savePath+s_fileName);
            out = new FileOutputStream(file);

            out.write(file_binary);
            for (int i=0; i < file_binary.length; i++){
                //out.write(file_binary)
            }
            out.close();
            b_return = true;
        } catch (FileNotFoundException e1) {
            log.error(e1.getMessage());
        } catch (IOException e) {
            log.error(e.getMessage());
        }finally{

        }
        return b_return;
    }

}
