namespace java com.gu.contententity.thrift.entity.game

struct Film {
  1: required string title
  2: required string year
  3: required string imdbId
  4: required list<string> directors
  5: required list<string> actors
  6: required string genre
}
