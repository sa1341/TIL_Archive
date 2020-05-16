## Spring JdbcTemplate을 사용한 Oracle BLOB Insert 예제코드


```java
  private void createBlobData(String alias) throws IOException {
        DataSource dataSource = dataSourceMap.get(alias);
        jdbcTemplate.setDataSource(dataSource);
        File f = new File("I:/Htf_test01.hwp.pdf");
        if (!f.exists()) {
            System.out.println("파일이 존재 하지 않습니다.");
            System.exit(1);
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        FileInputStream fis = new FileInputStream(f);
        while (true) {
            int x = fis.read();
            if (x == -1) break;
            bos.write(x);
        }
        fis.close();
        bos.close();
        final ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        String SQL = "insert into FILE_STORAGE(ID, FILE_NAME) values (?, ?, ?)";
       // jdbcTemplate.update("insert into FILE_STORAGE (ID, FILE_NAME) values(?, ?)",741, "sampleFile1");

        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                String sql = "insert into FILE_STORAGE (ID, FILE_NAME, FILEDATA) values(?, ?, ?)";
                PreparedStatement ps = connection.prepareStatement(sql.toString(),
                        Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, 1452);
                ps.setString(2, "blobFile");
                ps.setBlob(3, bis);
                return ps;
            }
        });
    }
```
