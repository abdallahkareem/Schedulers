public interface Tester<T extends TestCase> {
    void test(T testCase);
}
