package com.example.userservice.controller;
import com.example.userservice.model.Employee;
import com.example.userservice.model.Quiz_Student;
import com.example.userservice.repository.Quiz_StudentRepository;
import com.example.userservice.security.JwtFilter;
import com.example.userservice.security.JwtGenerator;
import com.example.userservice.service.PostService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/student")
public class Quiz_StudentController {

    @Autowired
    private Quiz_StudentRepository quiz_StudentRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private UserDetailsService userDetailsService;
    boolean result=false;
    int count =0;
    private JwtGenerator jwtGenerator;
    private JwtFilter jwtFilter;
    public Quiz_StudentController(JwtGenerator jwtGenerator,JwtFilter jwtFilter) {
        this.jwtGenerator = jwtGenerator;
        this.jwtFilter=jwtFilter;
    }
    @PostMapping("/insert")
    public ResponseEntity<Quiz_Student> insertData(@RequestBody Quiz_Student quiz_Student){
        Quiz_Student data=quiz_StudentRepository.save(quiz_Student);


        return ResponseEntity.ok(data);
    };
    @GetMapping("/getAll")
    public ResponseEntity<?> returnedData(@RequestParam String status) {
        List<Quiz_Student> data = quiz_StudentRepository.findAll();
        return ResponseEntity.ok(data);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(HttpServletRequest request, HttpServletResponse response,
                                                     FilterChain chain, @RequestBody Map<String, String> requestData) {
        //String filePath = "C:\\Users\\01957\\Downloads/abc.xlsx";
       // readExcelFile(filePath);
        //System.out.println("Authenticated User: " + requestData);
        // readCSVForInsertUser("C:\\Users\\01957\\Downloads/userData.csv");

        Map<String, String> responseData = new HashMap<>();


        String username = requestData.get("email");
        String password = requestData.get("password");

        Optional<Quiz_Student> data = quiz_StudentRepository.findByIdNumber(username);

        // Check if user exists
        if (data.isEmpty()) {
            responseData.put("error", "User not found or inactive.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseData);
        }

        Quiz_Student employee = data.get();
        // out.println(employee.getName()+"  "+employee.getPosition()+"  "+employee.getType());
        boolean result=false;


        // Validate password
        if (!passwordEncoder.matches(password, employee.getPassword())) {
            responseData.put("error", "Invalid credentials.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseData);
        }

        // Generate Tokens
        String token = jwtGenerator.generateToken(username);
        String refreshToken = jwtGenerator.generateRefreshToken(username);

        // Successful authentication response
        responseData.put("token", token);
        responseData.put("refreshToken", refreshToken);
        responseData.put("result", "Authenticated");
        if(username.equals("Admin")){
            responseData.put("role","Admin");
        }

        return ResponseEntity.ok(responseData);
    }

    public void readExcelFile(String filePath) {
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0); // First sheet

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Skip header row

                String id = formatCell(row.getCell(1));
                String password = formatCell(row.getCell(2));

                // Skip if id or password is null or empty
                if (id == null || id.trim().isEmpty() || password == null || password.trim().isEmpty()) {
                    System.out.println("⚠️ Skipping row " + (row.getRowNum() + 1) + " due to empty ID or Password.");
                    continue;
                }

                quiz_StudentRepository.save(new Quiz_Student(id, passwordEncoder.encode(password)));
                System.out.println("✅ Saved - ID: " + id + ", Password: " + password);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private  String formatCell(Cell cell) {
        if (cell == null) return "";

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                return String.valueOf((long) cell.getNumericCellValue()); // <- removes .0
            case FORMULA:
                if (cell.getCachedFormulaResultType() == CellType.NUMERIC) {
                    return String.valueOf((long) cell.getNumericCellValue());
                } else if (cell.getCachedFormulaResultType() == CellType.STRING) {
                    return cell.getStringCellValue().trim();
                }
                break;
            default:
                return cell.toString().trim();
        }
        return "";
    }
}
