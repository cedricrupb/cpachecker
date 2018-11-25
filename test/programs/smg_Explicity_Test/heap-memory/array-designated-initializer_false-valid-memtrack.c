typedef unsigned char __uint8_t;
typedef __uint8_t uint8_t;
struct const_passdb
{
  char const
  *filename;
  char def[9U];
  uint8_t off[9U];
  uint8_t numfields;
  uint8_t size_of;
};
static struct const_passdb const const_pw_db = { . filename = ( char const *) "/etc/passwd" ,
    . def = "SsIIsss" ,
    . off = { [ 0U ] = 0U , [ 1U ] = 8U , [ 2U ] = 16U ,
              [ 7U ] = 20U , 24U , [ 5U ] = 40U } ,
    . numfields = 7U , . size_of = 48U };

int main() {
    char *test;
    test = malloc(10);
    if (test) {
        if (const_pw_db.off[8] != 24) {
            free(test);
        }
    }
    return 0;
}
