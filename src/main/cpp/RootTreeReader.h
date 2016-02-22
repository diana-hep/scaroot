#include <string>

class RootTreeReader {
private:
  std::string mystuff;
public:
  void set(const std::string &stuff) { this->mystuff = stuff; }
  std::string get() { return mystuff; }
};
